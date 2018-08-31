package geoModel

import linearAlgebra.Vector3
import linearAlgebra.MatrixSolvLU.*

class InterpolatedBspline: Bspline {

    val pts = mutableListOf<Vector3>()
    
    constructor(max: Int): super(max)
    
    constructor(max: Int, p: MutableList<Vector3>): super(max, p) {
        this.pts = p
        evalPrm(pts); degree(); order(); evalKnots(); evalCtrlPoints()
    }

    override fun addPts(v: Vector3) {
        pts.add(v)
        evalPrm(pts); degree(); order(); evalKnots(); evalCtrlPoints()
    }

    override fun addPts(i: Int, v: Vector3) {
        pts.add(i, v)
        evalPrm(pts); degree(); order(); evalKnots(); evalCtrlPoints()
    }

    override fun removePts(i: Int) {
        if(i != -1) pts.removeAt(i)
        if(!pts.isEmpty()) { evalPrm(pts); degree(); order(); evalKnots(); evalCtrlPoints() }
    }

    //Algorithm 9.1
    private fun evalCtrlPoints()
    {
        // Evaluate B-spline control points by the given points on a curve
        val n = pts.size
        val aa  = Array(n) {DoubleArray(n)}
        for (i in pts.indices) {
            val span = findIndexSpan(n, prm[i])
            val nn = basisFuncs(span, prm[i])
            for (j in 0..degree) aa[i][span - degree + j] = nn[j]
        }
        val bb = Array(3) {DoubleArray(n)}
        for (i in pts.indices)
            for (j in 0..2) bb[j][i] = pts[i][j]
        if (n >= 3) {
            val indx = IntArray(n)
            ludcmp(n, aa, indx)
            for (j in 0..2) lubksb(n, aa, indx, bb[j])
        }
        for (i in pts.indices) ctrlPts[i] = Vector3(bb[0][i], bb[1][i], bb[2][i])
    }
}
