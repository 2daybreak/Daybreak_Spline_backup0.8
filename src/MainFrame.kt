import GeoModel.InterpolatedCurve
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

        val curve = JMenu("Spline")
        jMenuBar.add(curve)

        /*
        val tmp = JMenuItem("")
        draw.add(tmp)
        tmp.accelerator = KeyStroke.getKeyStroke(KeyEvent, ActionEvent)
        tmp.addActionListener{e: ActionEvent -> }
        */

        val line = JMenuItem("Line")
        curve.add(line)
        line.addActionListener{ e: ActionEvent -> TODO()}

        val circle = JMenuItem("Circle")
        curve.add(circle)
        circle.addActionListener{ e: ActionEvent ->
            //modelTree.mainPanel.circle.add(Circle(Vector3(300.0, 300.0, 0.0), 300.0))
        }

        val spline = JMenuItem("Spline")
        curve.add(spline)
        spline.addActionListener{ e: ActionEvent ->
            modelTree.mainPanel.spline.add(InterpolatedCurve(3))
            modelTree.mainPanel.ing = modelTree.mainPanel.spline.size - 1
            modelTree.mainPanel.mode = MainJPanel.Mode.Spline
            modelTree.geometry.add(DefaultMutableTreeNode("spline"))
            modelTree.model.reload()
            modelTree.tree.expandRow(0)
        }

        val nurbs = JMenuItem("Nurbs")
        curve.add(nurbs)
        nurbs.addActionListener{ e: ActionEvent ->
            //modelTree.mainPanel.nurbs.add(Nurbs(3))
            modelTree.mainPanel.ing = modelTree.mainPanel.nurbs.size - 1
            modelTree.mainPanel.mode = MainJPanel.Mode.Nurbs
            modelTree.geometry.add(DefaultMutableTreeNode("nurbs"))
            modelTree.model.reload()
            modelTree.tree.expandRow(0)
        }

        val surface = JMenu("Surface")
        jMenuBar.add(surface)

        val tools = JMenu("Tools")
        jMenuBar.add(tools)

        val window = JMenu("Window")
        jMenuBar.add(window)

        val dataTable = JMenuItem("DataTable")
        window.add(dataTable)
        dataTable.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK)
        dataTable.addActionListener{e: ActionEvent -> modelTree.mainPanel.subFrame.isVisible = true}

    }

}
