package geoModel

import linearAlgebra.Vector3
import linearAlgebra.binomialCoef

abstract class Nurbs(maxDeg: Int): Bspline(maxDeg) {

    /*  A Nurbs curve is defined by
        C(u) = Sum( Ni(u) * wi * Pi ) / Sum( Ni(u) * wi )
        where Pi are the control points, wi are the weights, and Ni(u) are the basis funcs defined on the knot vector */

    val wts = mutableListOf<Double>()

    protected abstract fun evalWeights()

    //Algorithm 4.1 mod
    private fun denominator(span: Int, t: Double, ni: DoubleArray): Double {
        var sum = 0.0
        for (j in 0..degree)
            sum += wts[span - degree + j] * ni[j]
        return sum
    }

    //Algorithm 4.1 mod
    private fun numerator(span: Int, t: Double, ni: DoubleArray): Vector3 {
        var sum = Vector3().zero
        for (j in 0..degree)
            sum += ctrlPts[span - degree + j] * ni[j] * wts[span - degree + j]
        return sum
    }

    //Algorithm 4.1
    override fun curvePoint(t: Double): Vector3 {
        val span = findIndexSpan(wts.size, t)
        val ni = basisFuncs(span, t)
        return numerator(span, t, ni) / denominator(span, t, ni)
    }

    //Algorithm 4.2 mod
    override fun curveDers(t: Double, kmax: Int): Array<Vector3> {
        if(kmax == 0) return Array(1) { curvePoint(t) }
        // Compute kth derivatives
        val v = Array(kmax + 1) { Vector3() }
        val span = findIndexSpan(wts.size, t)
        val nders = dersBasisFunc(span, t, kmax)
        v[0] = curvePoint(t)
        for(k in 1..kmax) {
            v[k] = numerator(span, t, nders[k])
            for(j in 1..k) {
                v[k] -= v[k - j] * binomialCoef(k, j) * denominator(span, t, nders[j])
            }
            v[k] = v[k] / denominator(span, t, nders[0])
        }
        return v
    }


}