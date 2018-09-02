package geoModel

import linearAlgebra.Vector3
import linearAlgebra.MatrixSolvLU.*

class InterpolatedBspline: Bspline {

    var pts = mutableListOf<Vector3>()

    constructor(): this(3)

    constructor(max: Int): super(max)

    constructor(max: Int, p: MutableList<Vector3>): super(max) {
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

    override fun removePts(i: Int) {
        if(i != -1) pts.removeAt(i)
        if(!pts.isEmpty()) { properties(pts); evalCtrlPoints() }
    }

    override fun curveKnotInsert(t: Double) {
        super.curveKnotInsert(t)
        val low: Int
        val span = findIndexSpan(pts.size, t)
        low = span - degree + 1
        for (i in 0 until degree) {
            if (i != degree - 1) {
                pts.removeAt(low + i)
            }
            pts.add(low + i, curvePoint(prm[low + i]))
        }
        properties(pts)
        //evalCtrlPoints()
    }

    private fun evalCtrlPoints()
    {
        // Evaluate B-spline control points by the given points on a curve
        ctrlPts.clear()
        //if(maxDeg==3) cubicTridiagonal()
        generalLU()
    }

    //Algorithm 9.1
    private fun generalLU() {
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
        for (i in pts.indices) ctrlPts.add(Vector3(bb[0][i], bb[1][i], bb[2][i]))
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