import geoModel.*
import linearAlgebra.Vector3
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode

class Menu(text: String, val panel: ModelTree, val i: Int) {

    val btm = JButton(text)
    val itm = JMenuItem(text)

    init {
        btm.addActionListener { e: ActionEvent ->
            panel.mainPanel.curve.add(when(i) {
                0 -> Circle(Vector3(), 100.0)
                1 -> Bspline()
                2 -> InterpolatedBspline()
                3 -> Nurbs()
                else -> InterpolatedNurbs()
            })
            panel.mainPanel.ing = panel.mainPanel.curve.size - 1
            panel.mainPanel.mode = MainJPanel.Mode.Curve
            panel.geometry.add(DefaultMutableTreeNode(btm.text))
            panel.model.reload()
            panel.tree.expandRow(0)
        }

        itm.addActionListener { e: ActionEvent ->
            panel.mainPanel.curve.add(when(i) {
                0 -> Circle(Vector3(), 100.0)
                1 -> Bspline()
                2 -> InterpolatedBspline()
                3 -> Nurbs()
                else -> InterpolatedNurbs()
            })
            panel.mainPanel.ing = panel.mainPanel.curve.size - 1
            panel.mainPanel.mode = MainJPanel.Mode.Curve
            panel.geometry.add(DefaultMutableTreeNode(itm.text))
            panel.model.reload()
            panel.tree.expandRow(0)
        }
    }
}