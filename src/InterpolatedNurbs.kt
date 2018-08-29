package geoModel

import linearAlgebra.MatrixSolvLU
import linearAlgebra.Vector3

class InterpolatedNurbs(maxDeg: Int): Nurbs(maxDeg) {

    val pts = mutableListOf<Vector3>()

    fun addPts(v: Vector3) {
        pts.add(v); ctrlPts.add(v)
        degree(); order(); evalPrm(); evalKnots(); evalWeights(); evalCtrlPoints()
    }

    fun addPts(i: Int, v: Vector3) {
        pts.add(i, v); ctrlPts.add(i, v)
        degree(); order(); evalPrm(); evalKnots(); evalWeights(); evalCtrlPoints()
    }

    fun removePts(i: Int) {
        pts.removeAt(i); ctrlPts.removeAt(i)
        if(!pts.isEmpty()) degree(); order(); evalPrm(); evalKnots(); evalWeights(); evalCtrlPoints()
    }

    override fun evalWeights() {
        wts.clear()
        for(i in 1..pts.count())
            wts.add(1.0)
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

    override fun evalKnots() {
        /*  Evaluate knot vector. For the uniform spacing, e.g.
        ctrlPts 1 (point)    : n=1,deg=0,order=1,knots={0,1}
        ctrlPts 2 (linear)   : n=2,deg=1,order=2,knots={0,0,1,1}
        ctrlPts 3 (quadratic): n=3,deg=2,order=3,knots={0,0,0,1,1,1}
        ctrlPts 4 (cubic)    : n=4,deg=3,order=4,knots={0,0,0,0,1,1,1,1}
        ctrlPts 5 (quartic)  : n=5,deg=4,order=5,knots={0,0,0,0,0,1,1,1,1,1}
        ctrlPts 6 (quintic)  : n=6,deg=5,order=6,knots={0,0,0,0,0,0,1,1,1,1,1,1}
        ctrlPts 7 (quintic)  : n=7,deg=5,order=6,knots={0,0,0,0,0,0,1/2,1,1,1,1,1,1}
        ctrlPts 8 (quintic)  : n=8,deg=5,order=6,knots={0,0,0,0,0,0,1/3,2/3,1,1,1,1,1,1}
        general   (quintic)  : n= ,deg=5,order=6,knots={...,[1/(n-deg),...,(n-order)/(n-deg)],...,} */
        knots.clear()
        for(i in 1..order) knots.add(0.toDouble())
        for(i in 1..order) knots.add(1.toDouble())
        for(i in 1..pts.size - order) {
            var interval = 0.0
            //averaging spacing(reflecting the distribution of prm)
            for(j in i until i + degree) interval += prm[j]
            interval /= degree
            knots.add(degree + i, interval)
        }
    }

    //Algorithm 9.1
    override fun evalCtrlPoints()
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
            MatrixSolvLU.ludcmp(n, aa, indx)
            for (j in 0..2) MatrixSolvLU.lubksb(n, aa, indx, bb[j])
        }
        for (i in pts.indices) ctrlPts[i] = Vector3(bb[0][i], bb[1][i], bb[2][i])
    }
}