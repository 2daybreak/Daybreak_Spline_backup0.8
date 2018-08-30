package geoModel

import linearAlgebra.Vector3
import linearAlgebra.MatrixSolvLU.*

class InterpolatedBspline(maxDeg: Int): Bspline(maxDeg) {

    val pts = mutableListOf<Vector3>()

    override fun addPts(v: Vector3) {
        pts.add(v); super.addPts(v); evalCtrlPoints()
    }

    override fun addPts(i: Int, v: Vector3) {
        pts.add(i, v); super.addPts(i, v); evalCtrlPoints()
    }

    override fun removePts(i: Int) {
        super.removePts(i)
        if(i != -1) pts.removeAt(i)
        if(!pts.isEmpty()) evalCtrlPoints()
    }

    override fun evalPrm() {
        prm.clear()
        var sum = 0.toDouble()
        prm.add(sum)
        //Chord length method
        for(i in 1 until pts.count())
        {
            val del = pts[i] - pts[i - 1]
            sum += del.length
        }
        for(i in 1 until pts.count())
        {
            val del = pts[i] - pts[i - 1]
            prm.add(prm[i - 1] + del.length / sum)
        }
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