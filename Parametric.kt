package geoModel

import linearAlgebra.Vector3
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D

abstract class Parametric {

    protected abstract var degree: Int

    abstract val prm: MutableList<Double>

    abstract val ctrlPts: MutableList<Vector3>

    abstract var endDers: Array<Vector3>

    abstract fun addPts(v: Vector3)

    abstract fun addPts(i: Int, v: Vector3)

    abstract fun removePts(i: Int)

    open operator fun invoke(t: Double): Vector3 {
        return curvePoint(t)
    }

    open operator fun invoke(kmax: Int, t: Double): Array<Vector3> {
        return curveDers(kmax, t)
    }

    open operator fun invoke(v: Vector3): Vector3 {
        return closestPoint(v)
    }

    // Currently, it only work for Bsplin class, not working for the inherited class
    fun split(t: Double) {
        for(i in 1..degree) curveKnotInsert(t)
    }

    /**
     * Calculate distance between the specified points, and then
     * assign the evaluated parameters to each point.
     *
     * @param p the specified points
     * @see Bspline
     */
    protected abstract fun properties(p: MutableList<Vector3>)

    /**
     * Return the point on a curve at a specified parameter
     *
     * @param t a specific parameter within a range from 0 to 1
     * @return the point on a curve
     * @see Bspline
     */
    protected abstract fun curvePoint(t: Double): Vector3

    /**
     * Return the derivatives on a curve at a specified parameter
     *
     * @param kmax the max. degree of the derivatives
     * @param t a specific parameter within a range from 0 to 1
     * @return the derivatives at a specified parameter
     * @see Bspline
     */
    protected abstract fun curveDers(kmax: Int, t: Double): Array<Vector3>

    /**
     * Return the closest point on a curve
     * from the position away from it
     *
     * @param v the position a distance away from a curve
     * @return the closest point on a curve
     * @see Bspline
     */
    protected abstract fun closestPoint(v: Vector3): Vector3

    protected abstract fun curveKnotInsert(t: Double)

    open fun draw(g: Graphics2D) {
        val c = this
        val size = 10.0
        val half = size / 2
        val linePerNode = 8

        g.stroke = BasicStroke()
        //Draw points
        if (c is InterpolatedBspline || c is InterpolatedNurbs)
            g.color = Color.YELLOW
        else
            g.color = Color.LIGHT_GRAY
            for (p in c.prm)
                g.draw(Ellipse2D.Double(c(p).x - half, c(p).y - half, size, size))
        //Draw control points
        if (c is InterpolatedBspline || c is InterpolatedNurbs)
            g.color = Color.LIGHT_GRAY
        else
            g.color = Color.YELLOW
        for (v in c.ctrlPts) g.draw(Ellipse2D.Double(v.x - half, v.y - half, size, size))
        //Draw control polygon
        if (c is InterpolatedBspline || c is InterpolatedNurbs)
            g.color = Color.GRAY
        else
            g.color = Color.WHITE
        for (i in 1 until c.ctrlPts.size) {
            g.drawLine(c.ctrlPts[i - 1].x.toInt(), c.ctrlPts[i - 1].y.toInt(),
                    c.ctrlPts[i].x.toInt(), c.ctrlPts[i].y.toInt())
        }
        //Draw curve (interpolation of pts)
        g.color = Color.CYAN
        val n = c.ctrlPts.size * linePerNode
        if (c.ctrlPts.size > 1) for (i in 1 until n) {
            val t1 = (i - 1).toDouble() / (n - 1).toDouble()
            val t2 = i.toDouble() / (n - 1).toDouble()
            val p1 = c(t1)
            val p2 = c(t2)
            g.drawLine(p1.x.toInt(), p1.y.toInt(), p2.x.toInt(), p2.y.toInt())
        }
        //Draw derivatives at each assigned parameter
        for (t in c.prm)
            for (i in 1..3) {
                g.color = when (i) {
                    1 -> Color.RED
                    2 -> Color.GREEN
                    3 -> Color.BLUE
                    else -> {
                        Color.GRAY
                    }
                }
                val p1 = c(t)
                var p2 = p1 + c(3, t)[i].normalize() * 30 //30 pixels
                g.drawLine(p1.x.toInt(), p1.y.toInt(), p2.x.toInt(), p2.y.toInt())
            }
    }
}