import geoModel.*
import linearAlgebra.Vector3
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelListener
import java.awt.event.MouseWheelEvent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.JScrollPane
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel

class MainJPanel: JPanel() {

    // Mouse, Key, Logic
    var ing = 0
    var u = Vector3()
    var clickPts = false
    var mode = Mode.View
    enum class Mode{View, Spline, Nurbs}

    // Geometry
    val spline = mutableListOf<InterpolatedCurve>()
    val nurbs  = mutableListOf<InterpolatedNurbs>()
    val circle  = mutableListOf<Circle>()

    // Table
    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    val subFrame = SubFrame(JScrollPane(table))

    // Viewport
    //var viewport = Viewport()
    val size = 20

    init {
        background = Color(30, 30, 30)
        addMouseListener(object: MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                when (e.clickCount) {
                    2 -> println("DoubleClicked")
                    3 -> println("TripleClicked")
                    4 -> println("QuadrupleClicked")
                }
            }
            override fun mouseExited (e: MouseEvent) { }
            override fun mouseEntered(e: MouseEvent) { }
            override fun mousePressed(e: MouseEvent) {

                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        val v = Vector3(e.x, e.y, 0)
                        u = v
                        when(mode) {
                            Mode.View -> {}
                            Mode.Spline -> {
                                for (p in spline[ing].pts) {
                                    if (Point3(size, p).contains(e.x, e.y)) {
                                        clickPts = true
                                        u = p
                                        break
                                    }
                                }
                                if (!clickPts) spline[ing].addPts(v)
                                clickPts = false
                            }
                        }

                    }
                    MouseEvent.BUTTON2 -> {}
                    MouseEvent.BUTTON3 -> {}
                }
            }
            override fun mouseReleased(e: MouseEvent) { repaint() }
        })
        addMouseMotionListener(object: MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
                when(mode) {
                    Mode.View -> {}
                    Mode.Spline -> {
                        val v = Vector3(e.x, e.y, 0)
                        val i = spline[ing].pts.indexOf(u)
                        println("i=$i")
                        spline[ing].removePts(i)
                        spline[ing].addPts(i, v)
                        u = v
                        repaint()
                    }
                }

            }
            override fun mouseMoved(e: MouseEvent) { }
        })
        addMouseWheelListener(object: MouseWheelListener {
            override fun mouseWheelMoved(e: MouseWheelEvent?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
        iniTable()
    }

    private fun iniTable() {
        table.background = Color(50, 50, 50)
        table.foreground = Color.lightGray
        tableModel.addTableModelListener { e: TableModelEvent ->
            val row = e.firstRow
            val v = DoubleArray(3)
            if (row > -1) {
                try {
                    for(i in 0..2) v[i] = table.getValueAt(row, i).toString().toDouble()
                    when(mode) {
                        Mode.Spline -> {
                            spline[ing].removePts(row)
                            spline[ing].addPts(row, Vector3(v[0], v[1], v[2]))
                        }
                    }

                } catch (e: NumberFormatException) {
                    println(e.message)
                } finally {
                    repaint()
                }

            }
        }
    }

    override fun paintComponent(g: Graphics) {
        updatePaint(g)
        updateTable()
    }

    private fun updatePaint(g: Graphics) {

        super.paintComponent(g)
        g as Graphics2D
        val size = 10.0
        val half = size / 2

        for (b in spline) {
            //Draw control polygon
            g.color = Color.GRAY; g.stroke = BasicStroke()
            for (i in 1 until b.ctrlPts.size) {
                g.drawLine( b.ctrlPts[i - 1].x.toInt(),
                        b.ctrlPts[i - 1].y.toInt(),
                        b.ctrlPts[i].x.toInt(),
                        b.ctrlPts[i].y.toInt())
            }
            //Draw B-spline curve (interpolation of pts)
            g.color = Color.CYAN; g.stroke = BasicStroke()
            if(b == spline[ing]) g.color = Color.YELLOW
            val n = b.pts.size * 8
            if(b.pts.size > 1) for (i in 1 until n) {
                val p1 = b.curvePoint((i - 1).toDouble() / (n - 1).toDouble())
                val p2 = b.curvePoint(      i.toDouble() / (n - 1).toDouble())
                g.drawLine(p1.x.toInt(),
                        p1.y.toInt(),
                        p2.x.toInt(),
                        p2.y.toInt())
            }
            //Draw ctrlPts
            g.color = Color.LIGHT_GRAY; g.stroke = BasicStroke()
            for (v in b.ctrlPts) g.draw(Ellipse2D.Double(v.x - half, v.y - half, size, size))
            //Draw pts
            g.color = Color.YELLOW; g.stroke = BasicStroke()
            for(v in b.pts) g.draw(Ellipse2D.Double(v.x - half, v.y - half, size, size))
            //Draw derivatives at t = 0.5
            for(i in 1..3) {
                g.color = when(i) {
                    1 -> Color.RED
                    2 -> Color.GREEN
                    3 -> Color.BLUE
                    else -> { Color.GRAY }
                }
                val p1 = b.curvePoint(0.5)
                val p2 = b.curveDers(0.5, 3)[i] + p1
                g.drawLine(p1.x.toInt(),
                        p1.y.toInt(),
                        p2.x.toInt(),
                        p2.y.toInt())
            }
        }

        for (b in circle) {
            //Draw control polygon
            g.color = Color.GRAY; g.stroke = BasicStroke()
            for (i in 1 until b.ctrlPts.size) {
                g.drawLine( b.ctrlPts[i - 1].x.toInt(),
                        b.ctrlPts[i - 1].y.toInt(),
                        b.ctrlPts[i].x.toInt(),
                        b.ctrlPts[i].y.toInt())
            }
            //Draw Nurbs curve (interpolation of pts)
            g.color = Color.CYAN
            val n =25
            for(i in 1 until n) {
                val p1 = b.curvePoint((i - 1).toDouble() / (n - 1).toDouble())
                val p2 = b.curvePoint(      i.toDouble() / (n - 1).toDouble())
                g.drawLine(p1.x.toInt(),
                        p1.y.toInt(),
                        p2.x.toInt(),
                        p2.y.toInt())
            }
            //Draw ctrlPts
            g.color = Color.LIGHT_GRAY; g.stroke = BasicStroke()
            for (v in b.ctrlPts) g.draw(Ellipse2D.Double(v.x - half, v.y - half, size, size))
            //Draw derivatives at t = 0.5
            for(i in 1..3) {
                g.color = when(i) {
                    1 -> Color.RED
                    2 -> Color.GREEN
                    3 -> Color.BLUE
                    else -> { Color.GRAY }
                }
                val p1 = b.curvePoint(0.5)
                val p2 = b.curveDers(0.5, 3)[i] + p1
                g.drawLine(p1.x.toInt(),
                        p1.y.toInt(),
                        p2.x.toInt(),
                        p2.y.toInt())
            }
        }

    }

    private fun updateTable() {
        val list = mutableListOf<Array<Double>>()
        when(mode) {
            Mode.View -> {}
            Mode.Spline -> for(p in spline[ing].pts) list.add(arrayOf(p.x, p.y, p.z))
        }
        val data = list.toTypedArray()
        val head = arrayOf("x", "y", "z")
        tableModel.setDataVector(data,head)
    }

}