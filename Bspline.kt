package geoModel

import linearAlgebra.Vector3
import kotlin.math.abs

open class Bspline: Parametric {

    /*  A B-Spline curve is defined by
        C(t) = Sum( Ni(t) * Pi )
        where Pi are the control points, and Ni(t) are the basis funcs defined on the knot vector */

    final override var prm = mutableListOf<Double>()
    final override var ctrlPts = mutableListOf<Vector3>()
    final override var endDers = arrayOf(Vector3().unitY*500, Vector3())
    protected val knots = mutableListOf<Double>()
    /**
     * Original : prm to be assigned by chord length, and then knots to be evaluated by prm
     * Covariant: prm to be assigned by averaging knots having uniform distribution
     */
    protected val isCovariant = false
    protected var chord = 0.0
    protected var order = 0
    override var degree = -1
    private var maxDeg = 0

    constructor() : this(3)

    constructor(max: Int) {
        maxDeg = max
    }

    constructor(max: Int, p: MutableList<Vector3>) {
        maxDeg = max
        this.ctrlPts = p
        properties(ctrlPts)
    }

    final override fun properties(p: MutableList<Vector3>) {
        degree(p)
        order()
        if(isCovariant) {
            uniformKnots(p.size)
            evalPrmKnotAverages(p.size)
        }
        else {
            evalPrm(p)
            evalKnots()
        }

    }

    override fun addPts(v: Vector3) {
        ctrlPts.add(v)
        properties(ctrlPts)
    }

    override fun addPts(i: Int, v: Vector3) {
        ctrlPts.add(i, v)
        properties(ctrlPts)
    }

    override fun removePts(i: Int) {
        if (i != -1) ctrlPts.removeAt(i)
        if (!ctrlPts.isEmpty()) {
            properties(ctrlPts)
        }
    }

    private fun degree(p: MutableList<Vector3>) {
        //Once degree is fixed, the knots completely determine the basis funcs.
        val nm1 = p.size - 1
        degree = when (nm1 > maxDeg) {
            true -> maxDeg
            false -> nm1
        }
    }

    private fun order() {
        order = degree + 1
    }

    protected fun evalPrm(p: MutableList<Vector3>) {
        prm.clear()
        var sum = 0.toDouble()
        prm.add(sum)
        //Chord length method
        for (i in 1 until p.count()) {
            val del = p[i] - p[i - 1]
            sum += del.length
        }
        for (i in 1 until p.count()) {
            val del = p[i] - p[i - 1]
            prm.add(prm[i - 1] + del.length / sum)
        }
        chord = sum
    }

    protected fun evalKnots() {
        knots.clear()
        for (i in 1..order) knots.add(0.toDouble())
        for (i in 1..order) knots.add(1.toDouble())
        for (i in 1..prm.size - order) {
            var interval = 0.0
            //averaging spacing(reflecting the distribution of prm)
            for (j in i until i + degree) interval += prm[j]
            interval /= degree
            knots.add(degree + i, interval)
        }
    }

    private fun uniformKnots(n: Int) {
        /*  For the uniform spacing,
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
        for (i in 1..order) knots.add(0.toDouble())
        for (i in 1..order) knots.add(1.toDouble())
        for (i in 1..n - order) {
            var interval = i.toDouble() / (n - degree)
            knots.add(degree + i, interval)
        }
    }

    protected fun evalPrmKnotAverages(n: Int) {
        prm.clear()
        for (i in 0 until n) {
            var average = 0.0
            for (j in 1..degree) {
                average += knots[i + j]
            }
            average /= degree
            prm.add(average)
        }
    }

    //Algorithm 2.1
    protected fun findIndexSpan(n: Int, t: Double): Int {
        //Determine the knot span index
        val useBinary = false
        val nm1 = n - 1
        //Make sure the parameter t is within the knots range
        if (t >= knots.max() ?: 1.0) return nm1 //special case of t at the curve end
        if (t < knots.min() ?: 0.0) return degree
        //Find index of ith knot span(half-open interval)
        var low: Int = degree
        var high: Int = nm1 + 1
        var mid: Int = (high + low) / 2
        if (useBinary)
        //Do binary search
            while (t < knots[mid] || t >= knots[mid + 1]) {
                if (t < knots[mid])
                    high = mid
                else
                    low = mid
                mid = (high + low) / 2
                //println("stuck in while loop: t = $t, mid=${knots[mid]}, mid+1=${knots[mid+1]}")
            }
        else
        //Do linear search
            for (i in knots.indices) if (t < knots[i]) {
                mid = i - 1; break
            }
        return mid
    }

    //Algorithm 2.2
    protected fun basisFuncs(span: Int, t: Double): DoubleArray {
        //Compute nonvanishing basis functions
        val left = DoubleArray(order)
        val right = DoubleArray(order)
        val ni = DoubleArray(order)
        ni[0] = 1.0
        for (j in 1..degree) {
            left[j] = t - knots[span + 1 - j]
            right[j] = knots[span + j] - t
            var saved = 0.0
            for (k in 0 until j) {
                val tmp = ni[k] / (right[k + 1] + left[j - k])
                ni[k] = saved + right[k + 1] * tmp
                saved = left[j - k] * tmp
            }
            ni[j] = saved
        }
        return ni
    }

    //Algorithm 2.3
    protected fun dersBasisFunc(span: Int, t: Double, kmax: Int): Array<DoubleArray> {
        /* Compute nonzero basis functions and their derivatives
        ders[k][j] is the kth derivative where 0 <= k <= kmax and 0 <= j <= degree
        First section is A.2.2 modified to store functions and knot differences.  */
        val ders = Array(kmax + 1, { DoubleArray(order) })
        var d: Double
        val left = DoubleArray(order)
        val right = DoubleArray(order)
        // basis functions and knot differences
        val ndu = Array(order, { DoubleArray(order) })
        // two most recently computed rows a(k,j) and a(k-1,j)
        val a = Array(2, { DoubleArray(order) })
        var tmp: Double
        var saved: Double
        ndu[0][0] = 1.0
        for (j in 1..degree) {
            left[j] = t - knots[span + 1 - j]
            right[j] = knots[span + j] - t
            saved = 0.0
            for (r in 0 until j) {
                // Lower triangle
                ndu[j][r] = right[r + 1] + left[j - r]
                tmp = ndu[r][j - 1] / ndu[j][r]
                // Upper triangle
                ndu[r][j] = saved + right[r + 1] * tmp
                saved = left[j - r] * tmp
            }
            ndu[j][j] = saved;
        }
        // Load the basis functions
        for (j in 0..degree) {
            ders[0][j] = ndu[j][degree]
        }
        // This section computes the derivatives (Eq. [2.9])
        for (r in 0..degree) //Loop over function index
        {
            var s1 = 0;
            var s2 = 1 // Alternative rows in array a
            a[0][0] = 1.0
            // Loop to compute kth derivative
            for (k in 1..kmax) {
                d = 0.0
                var rk = r - k
                val pk = degree - k
                if (r >= k) {
                    a[s2][0] = a[s1][0] / ndu[pk + 1][rk]
                    d = a[s2][0] * ndu[rk][pk]
                }
                val j1 = when (rk >= -1) {
                    true -> 1
                    false -> -rk
                }
                val j2 = when (r - 1 <= pk) {
                    true -> k - 1
                    false -> degree - r
                }
                for (j in j1..j2) {
                    a[s2][j] = (a[s1][j] - a[s1][j - 1]) / ndu[pk + 1][rk + j]
                    d += a[s2][j] * ndu[rk + j][pk]
                }
                if (r <= pk) {
                    a[s2][k] = -a[s1][k - 1] / ndu[pk + 1][r]
                    d += a[s2][k] * ndu[r][pk]
                }
                ders[k][r] = d
                rk = s1; s1 = s2; s2 = rk //Switch rows
            }
        }
        // Multiply through by the correct factors (Eq. [2.9])
        var cf = degree
        for (k in 1..kmax) {
            for (j in 0..degree) ders[k][j] *= cf.toDouble()
            cf *= (degree - k)
        }
        return ders
    }

    //Algorithm 3.1
    override fun curvePoint(t: Double): Vector3 {
        val span = findIndexSpan(ctrlPts.size, t)
        val nn = basisFuncs(span, t)
        var v = Vector3().zero
        for (j in 0..degree) {
            v += ctrlPts[span - degree + j] * nn[j]
        }
        return v
    }

    //Algorithm 3.2
    override fun curveDers(kmax: Int, t: Double): Array<Vector3> {
        // Compute kth derivatives
        val v = Array(kmax + 1) { Vector3() }
        /* Allow kmax > degree, although the ders. are 0 in this case for nonrational curves,
            but these ders. are needed for rational curves */
        val du = minOf(kmax, degree)
        for (k in order..kmax) v[k] = Vector3().zero
        val span = findIndexSpan(ctrlPts.size, t)
        val nders = dersBasisFunc(span, t, du)
        for (k in 0..du) {
            v[k] = Vector3().zero
            for (j in 0..degree) {
                v[k] += ctrlPts[span - degree + j] * nders[k][j]
            }
        }
        return v
    }

    //Algorithm 5.1
    override fun curveKnotInsert(t: Double) {
        val low: Int
        val q = Array(order) {Vector3()}
        val span = findIndexSpan(ctrlPts.size, t)
        for (i in 0..degree) {
            val tmp: Vector3 = ctrlPts[span - degree + i]
            q[i] = tmp
        }
        low = span - degree + 1
        for (i in 0 until degree) {
            val alpha = (t - knots[low + i]) / (knots[span + 1 + i] - knots[low + i])
            q[i] = q[i + 1] * alpha + q[i] * (1.0f - alpha)
        }
        knots.add(span + 1, t)
        for (i in 0 until degree) {
            if (i != degree - 1) {
                ctrlPts.removeAt(low + i)
            }
            ctrlPts.add(low + i, q[i])
        }
        //if(isCovariant) { uniformKnots(ctrlPts.size); evalPrmKnotAverages(ctrlPts.size) } not appropriate
        degree(ctrlPts)
        order()
    }

    override fun closestPoint(v: Vector3): Vector3 {
        //Initial search
        val n = 8 * ctrlPts.size
        val c = this
        var t = 0.0
        var min = (c(0.0) - v).length
        for (i in 1..n) {
            val d = i.toDouble() / n
            val dum = (c(d) - v).length
            if (dum < min) {
                min = dum
                t = d
            }
        }
        //Minimum distance
        var u = curveDers(2, t)
        var isOrthogonal = false
        var isConverged = false
        var t0 = 0.0
        var i = 0
        while (!(isOrthogonal || isConverged)) {
            val delx = u[1].dot(u[0] - v) / (u[2].dot(u[0] - v) + u[1].dot(u[1]))
            t -= delx
            if (t >= knots.max() ?: 1.0) t = 1.0
            if (t < knots.min() ?: 0.0) t = 0.0
            u = curveDers(2, t)
            var res = abs(u[1].dot(u[0] - v))
            isOrthogonal = res < 1E-9
            print("Residual: orthogonal = $res ")
            res = (u[1] * (t - t0)).length
            isConverged = res < 1E-9
            println("convergence = $res ")
            t0 = t
            i += 1
            if (i > 20) break
        }
        println("$isOrthogonal, $isConverged")
        return c(t)
    }
}