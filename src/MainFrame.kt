import geoModel.*
import linearAlgebra.Vector3
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.* //Swing: Light weight component
import javax.swing.tree.DefaultMutableTreeNode

class MainFrame : JFrame() {

    //private val mainPanel = MainJPanel()
    private val modelTree = ModelTree()
    private val splitPane = JSplitPane(1, modelTree, modelTree.mainPanel)

    init {

        title = "MainFrame"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
        contentPane.add(splitPane)
        splitPane.dividerSize = 3
        splitPane.dividerLocation = 150
        contentPane.addKeyListener(KeyHandler())
        contentPane.requestFocus()

        MainMenuBar()

        setSize(1024, 600)
    }

    private fun MainMenuBar() {

        this.jMenuBar = JMenuBar()

        val file = JMenu("File")
        jMenuBar.add(file)

        val new = JMenuItem("New")
        file.add(new)
        new.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)
        new.addActionListener{e: ActionEvent -> TODO()}

        val save = JMenuItem("Save")
        file.add(save)
        save.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)
        save.addActionListener{e: ActionEvent -> TODO()}

        val saveAs = JMenuItem("Save As")
        file.add(saveAs)
        saveAs.accelerator = KeyStroke.getKeyStroke("F12")
        saveAs.addActionListener{e: ActionEvent -> TODO()}

        val open = JMenuItem("Open")
        file.add(open)
        open.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)
        open.addActionListener{e: ActionEvent -> TODO()}

        file.addSeparator()
        val exit = JMenuItem("Exit")
        file.add(exit)
        exit.addActionListener{e: ActionEvent -> TODO() }

        val edit = JMenu("Edit")
        jMenuBar.add(edit)

        val undo = JMenuItem("Undo")
        edit.add(undo)
        undo.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK)
        undo.addActionListener{e: ActionEvent -> TODO()}

        val redo = JMenuItem("Redo")
        edit.add(redo)
        redo.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK)
        redo.addActionListener{e: ActionEvent -> TODO()}

        edit.addSeparator()

        val cut = JMenuItem("Cut")
        edit.add(cut)
        cut.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK)
        cut.addActionListener{ e: ActionEvent -> TODO() }

        val copy = JMenuItem("Copy")
        edit.add(copy)
        copy.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK)
        copy.addActionListener{e: ActionEvent -> TODO()}

        val paste = JMenuItem("Paste")
        edit.add(paste)
        paste.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK)
        paste.addActionListener{ e: ActionEvent -> TODO()}

        val join = JMenuItem("Join")
        edit.add(join)
        join.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK)
        join.addActionListener{ e: ActionEvent -> TODO()}

        val split = JMenuItem("Split")
        edit.add(split)
        split.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK)
        split.addActionListener{ e: ActionEvent -> TODO()}

        val curve = JMenu("Curve")
        jMenuBar.add(curve)

        val line = JMenuItem("Line")
        line.addActionListener{ e: ActionEvent -> modelTree.mainPanel.curve.add(Line())
            modelTree.mainPanel.ing = modelTree.mainPanel.curve.size - 1
            modelTree.mainPanel.mode = MainJPanel.Mode.Curve
            modelTree.geometry.add(DefaultMutableTreeNode("line"))
            modelTree.model.reload()
            modelTree.tree.expandRow(0)
        }
        curve.add(line)

        val circle = JMenuItem("Circle")
        circle.addActionListener{ e: ActionEvent ->
            modelTree.mainPanel.curve.add(Circle(Vector3(300.0, 300.0, 0.0), 300.0))
        }
        curve.add(circle)

        val spline = JMenu("Spline")
        curve.add(spline)

        val ctrlBspline = JMenuItem("Control Points")
        ctrlBspline.addActionListener{ e: ActionEvent ->
            modelTree.mainPanel.curve.add(Bspline(3))
            modelTree.mainPanel.ing = modelTree.mainPanel.curve.size - 1
            modelTree.mainPanel.mode = MainJPanel.Mode.Curve
            modelTree.geometry.add(DefaultMutableTreeNode("spline"))
            modelTree.model.reload()
            modelTree.tree.expandRow(0)
        }
        spline.add(ctrlBspline)

        val passBspline = JMenuItem("Passing Points")
        passBspline.addActionListener{ e: ActionEvent ->
            modelTree.mainPanel.curve.add(InterpolatedBspline(3))
            modelTree.mainPanel.ing = modelTree.mainPanel.curve.size - 1
            modelTree.mainPanel.mode = MainJPanel.Mode.Curve
            modelTree.geometry.add(DefaultMutableTreeNode("spline"))
            modelTree.model.reload()
            modelTree.tree.expandRow(0)
        }
        spline.add(passBspline)

        val nurbs = JMenu("Nurbs")
        curve.add(nurbs)

        val ctrlNurbs = JMenuItem("Control Points")
        ctrlNurbs.addActionListener{ e: ActionEvent ->
            modelTree.mainPanel.curve.add(Nurbs(3))
            modelTree.mainPanel.ing = modelTree.mainPanel.curve.size - 1
            modelTree.mainPanel.mode = MainJPanel.Mode.Curve
            modelTree.geometry.add(DefaultMutableTreeNode("nurbs"))
            modelTree.model.reload()
            modelTree.tree.expandRow(0)
        }
        nurbs.add(ctrlNurbs)

        val passNurbs = JMenuItem("Passing Points")
        passNurbs.addActionListener{ e: ActionEvent ->
            modelTree.mainPanel.curve.add(InterpolatedNurbs(3))
            modelTree.mainPanel.ing = modelTree.mainPanel.curve.size - 1
            modelTree.mainPanel.mode = MainJPanel.Mode.Curve
            modelTree.geometry.add(DefaultMutableTreeNode("nurbs"))
            modelTree.model.reload()
            modelTree.tree.expandRow(0)
        }
        nurbs.add(passNurbs)

        val surface = JMenu("Surface")
        jMenuBar.add(surface)

        val tools = JMenu("Tools")
        jMenuBar.add(tools)

        val window = JMenu("Window")
        jMenuBar.add(window)

        val dataTable = JMenuItem("DataTable")
        dataTable.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK)
        dataTable.addActionListener{e: ActionEvent -> modelTree.mainPanel.subFrame.isVisible = true}
        window.add(dataTable)
    }

}
