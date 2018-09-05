package geoModel

import linearAlgebra.Vector3
import linearAlgebra.MatrixSolvLU.*

class InterpolatedBspline: Bspline {

    var pts = mutableListOf<Vector3>()
    var endDers = arrayOf(Vector3().unitX, Vector3().unitX)
    var chord = doubleArrayOf(0.0, 0.0)

    constructor() : this(3)

    constructor(max: Int) : super(max)

    constructor(max: Int, p: MutableList<Vector3>) : super(max) {
        this.pts = p
        properties(pts); evalCtrlPoints()
    }

    override fun addPts(v: Vector3) {
        pts.add(v)
        properties(pts); evalCtrlPoints()
    }

    override fun addPts(i: Int, v: Vector3) {
        pts.add(i, v)
        properties(pts); evalCtrlPoints()
    }

    override fun modPts(i: Int, v: Vector3) {
        pts[i] = v
        properties(pts)
    }

    override fun removePts(i: Int) {
        if (i != -1) pts.removeAt(i)
        if (!pts.isEmpty()) {
            properties(pts); evalCtrlPoints()
        }
    }

    override fun evalKnots() {
        if(prm.size <= 2) super.evalKnots()
        else {
            knots.clear()
            for (i in 1..order) knots.add(0.toDouble())
            for (i in 1..order) knots.add(1.toDouble())
            for (i in 0..prm.size - degree) {
                var interval = 0.0
                //averaging spacing(reflecting the distribution of prm)
                for (j in i until i + degree) interval += prm[j]
                interval /= degree
                knots.add(degree + i + 1, interval)
            }
        }
    }

    private fun evalCtrlPoints() {
        // Evaluate B-spline control points by the given points on a curve
        ctrlPts.clear()
        generalLU()
    }

    //Algorithm 9.1
    private fun generalLU() {
        val n = pts.size
        if (n >= 3) {
            chord[0] = 0.5 * (pts[1] - pts[0]).length
            chord[1] = 0.5 * (pts[n - 1] - pts[n - 2]).length
        }
        if (pts.size <= 2) {
            /*Solve linear algebra of AX = B, Sum( Ni(Ui) * Pi ) = Qi
            i = 0..n, where n = count() - 1 */
            val aa = Array(n) { DoubleArray(n) }
            for (i in 0 until n) {
                val span = findIndexSpan(n, prm[i])
                val nn = basisFuncs(span, prm[i])
                for (j in 0..degree) aa[i][span - degree + j] = nn[j]
            }
            val bb = Array(3) { DoubleArray(n) }
            for (i in 0 until n)
                for (j in 0..2) bb[j][i] = pts[i][j]
            if (n >= 3) {
                val indx = IntArray(n)
                ludcmp(n, aa, indx)
                for (j in 0..2) lubksb(n, aa, indx, bb[j])
            }
            for (i in 0 until n) ctrlPts.add(Vector3(bb[0][i], bb[1][i], bb[2][i]))
        } else {
            /*Solve linear algebra of AX = B, Sum( Ni(Uk) * Pi ) = Qk
            Note that basis (& also knots), ctrlPts are in i = 0..n+2,
            while prm, pts are in k = 0..n */
            val np2 = n + 2
            if(isCovariant) {
                uniformKnots(np2)
            }
            val aa = Array(np2) { DoubleArray(np2) }
            for (i in 0 until n) {
                val span = findIndexSpan(np2, prm[i])
                val nn = basisFuncs(span, prm[i])
                for (j in 0..degree) aa[i + 1][span - degree + j] = nn[j]
            }
            aa[0][0] = -1.0
            aa[0][1] = 1.0
            //aa[1][0] = 1.0
            //aa[n][n + 1] = 1.0
            aa[n + 1][n] = -1.0
            aa[n + 1][n + 1] = 1.0
            val bb = Array(3) { DoubleArray(np2) }
            for (j in 0..2) {
                for (i in 1 until n - 1) bb[j][i + 1] = pts[i][j]
                bb[j][0] = endDers[0][j] * chord[0]
                bb[j][1] = pts[0][j]
                bb[j][n] = pts[n - 1][j]
                bb[j][n + 1] = endDers[1][j] * chord[1]
            }
            val indx = IntArray(np2)
            ludcmp(np2, aa, indx)
            for (j in 0..2) lubksb(np2, aa, indx, bb[j])
            for (i in 0 until np2) ctrlPts.add(Vector3(bb[0][i], bb[1][i], bb[2][i]))
        }
    }

    //Algorithm 9.2
    private fun cubicTridiagonal() {
        /*
            val n = pts.size
            val nm1 = n - 1
            val bb = Array(3) {DoubleArray(n+2)}
            for (i in 3..nm1)
                for (j in 0..2) bb[j][i] = pts[i][j-1]
            val nn = basisFuncs(4,knots[4])
            var den = nn[1]
            ctrlPts[1] =
            */
        TODO()
    }
}