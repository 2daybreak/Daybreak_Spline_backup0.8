import java.util.*

class Bspline_old
/// <summary>
/// deg = 0 : Ni are step func.
/// deg = 1 : linear basis func.
/// deg = 2 : quadratic basis func.
/// deg = 3 : cubic basis func.
/// deg = 4 : quartic  basis func.
/// deg = 5 : quintic basis func.
/// /// </summary>
/*
(private val maxDegree: Int): Curve_prm() {

    private var degree: Int = 0
    private val knots = LinkedList<Float>()
    private val ctrlPts = LinkedList<Vector3>()

    fun order(): Int {
        return degree + 1
    }

    fun degree(): Int {
        // Once the degree is fixed, the knots completely determine the basis funcs.
        return degree
    }

    fun knots(): LinkedList<Float> {
        return knots
    }

    fun ctrlPts(): LinkedList<Vector3> {
        return ctrlPts
    }

    override fun addPts(v: Vector3) {
        evalKnots()
        evalCtrlPoints()
    }

    override fun removePts(v: Vector3) {
        if (Pts.isEmpty()) {
            evalKnots()
            evalCtrlPoints()
        }
    }

    fun EvalParamKnotAverages() {
        val n = pts().size() - 1
        prm().clear()
        for (i in 0..n) {
            var average = 0f
            for (j in 1..degree()) {
                average += knots[i + j]
            }
            average /= degree().toFloat()
            prm().add(average)
        }
    }

    // Evaluate knot vector based on the number of pts(not ctrlPts)
    private fun evalKnots() {
        knots.clear()
        val n = pts().size() - 1
        if (n < maxDegree) degree = n else degree = maxDegree
        for (i in 0 until order()) knots.add(0.0f)
        for (i in 0 until order()) knots.add(1.0f)
        for (i in 0 until pts().size() - order()) {
            var interval = 0f
            /*
            //uniform spacing
            interval = (float)(i + 1)
                     / (float)(ctrlPts.size() - degree() + 1);
            */
            // averaging spacing(reflecting the distribution of param.)
            for (j in i + 1..i + degree()) {
                interval += prm().get(j)
            }
            interval /= degree().toFloat()

            knots.add(order() + i, interval)
        }
    }

    // Algorithm 2.1
    protected fun FindIndexSpan(t: Float): Int {
        var t = t
        // Make sure the parameter t is within the knots range
        val max = Collections.max(knots)
        val min = Collections.min(knots)
        if (t > max) t = 1.0f //t = max;
        if (t < min) t = 0.0f //t = min;
        // Find index of ith knot span(half-open internal)
        val n = pts().size() - 1
        if (t == knots[n + 1]) return n // special case of t at the curve end
        var low = degree()
        var high = n + 1
        var mid = (high + low) / 2
        // Do binary search
        while (t < knots[mid] || t >= knots[mid + 1]) {
            if (t < knots[mid])
                high = mid
            else
                low = mid
            mid = (high + low) / 2
            println("stuck in while loop : t, mid, mid+1 = " + t + "," + knots[mid] + "," + knots[mid + 1])
        }
        return mid
    }

    // Algorithm 2.2
    protected fun BasisFuncs(span: Int, t: Float, N: FloatArray) {
        // Compute the nonvanishing basis functions
        val left = FloatArray(order())
        val right = FloatArray(order())
        var tmp: Float
        var saved: Float
        N[0] = 1.0f
        for (j in 1..degree()) {
            left[j] = t - knots[span + 1 - j]
            right[j] = knots[span + j] - t
            saved = 0.0f
            for (k in 0 until j) {
                tmp = N[k] / (right[k + 1] + left[j - k])
                N[k] = saved + right[k + 1] * tmp
                saved = left[j - k] * tmp
            }
            N[j] = saved
        }
    }

    // Algorithm 2.3
    protected fun DersBasisFuncs(span: Int, t: Float, kmax: Int, ders: Array<FloatArray>) {
        var ders = ders
        // Compute nonzero basis functions and their derivatives
        // ders[k][j] is the kth derivative where 0 <= k <= kmax and 0 <= j <= degree
        // First section is A.2.2 modified to store functions and knot differnces.
        ders = Array(kmax + 1) { FloatArray(order()) }
        var s1: Int
        var s2: Int
        var rk: Int
        var pk: Int
        var j1: Int
        var j2: Int
        var d: Float
        val left = FloatArray(order())
        val right = FloatArray(order())
        // basis functions and knot differences
        val ndu = Array(order()) { FloatArray(order()) }
        // two most recently computed rows a(k,j) and a(k-1,j)
        val a = Array(2) { FloatArray(order()) }
        var tmp: Float
        var saved: Float
        ndu[0][0] = 1.0f
        for (j in 1..degree()) {
            left[j] = t - knots[span + 1 - j]
            right[j] = knots[span + j] - t
            saved = 0.0f
            for (r in 0 until j) {
                // Lower triangle
                ndu[j][r] = right[r + 1] + left[j - r]
                tmp = ndu[r][j - 1] / ndu[j][r]
                // Upper triangle
                ndu[r][j] = saved + right[r + 1] * tmp
                saved = left[j - r] * tmp
            }
            ndu[j][j] = saved
        }
        // Load the basis functions
        for (j in 0..degree()) {
            ders[0][j] = ndu[j][degree()]
        }
        // This section computes the derivatives (Eq. [2.9])
        for (r in 0..degree())
        //Loop over function index
        {
            s1 = 0
            s2 = 1 // Alternative rows in array a
            a[0][0] = 1.0f
            // Loop to compute kth derivative
            for (k in 1..kmax) {
                d = 0.0f
                rk = r - k
                pk = degree() - k
                if (r >= k) {
                    a[s2][0] = a[s1][0] / ndu[pk + 1][rk]
                    d = a[s2][0] * ndu[rk][pk]
                }
                if (rk >= -1)
                    j1 = 1
                else
                    j1 = -rk
                if (r - 1 <= pk)
                    j2 = k - 1
                else
                    j2 = degree() - r
                for (j in j1..j2) {
                    a[s2][j] = (a[s1][j] - a[s1][j - 1]) / ndu[pk + 1][rk + j]
                    d += a[s2][j] * ndu[rk + j][pk]
                }
                if (r <= pk) {
                    a[s2][k] = -a[s1][k - 1] / ndu[pk + 1][r]
                    d += a[s2][k] * ndu[r][pk]
                }
                ders[k][r] = d
                rk = s1
                s1 = s2
                s2 = rk //Switch rows
            }
        }
        // Multiply through by the correct factors (Eq. [2.9])
        rk = degree()
        for (k in 1..kmax) {
            for (j in 0..degree()) ders[k][j] *= rk.toFloat()
            rk *= degree() - k
        }
    }

    // Algorithm 3.1
    fun CurvePoint(t: Float): Vector3 {
        val span = FindIndexSpan(t)
        val N = FloatArray(order())
        BasisFuncs(span, t, N)
        val v = Vector3.Zero
        for (j in 0..degree()) {
            v.plus(ctrlPts[span - degree() + j].scl(N[j]))
        }
        return v
    }

    // Algorithm 3.2
    fun CurveDers(t: Float, kmax: Int, v: Array<Vector3>) {
        var v = v
        // Compute kth derivatives
        v = arrayOfNulls<Vector3>(kmax + 1)
        val nders = Array(kmax + 1) { FloatArray(order()) }
        /* Allow kmax > degree, although the ders. are 0 in this case for nonrational curves,
            but these ders. are needed for rational curves */
        val du = Math.min(kmax, degree())
        for (k in order()..kmax) v[k] = Vector3.Zero
        val span = FindIndexSpan(t)
        DersBasisFuncs(span, t, du, nders)
        for (k in 0..du) {
            v[k] = Vector3.Zero
            for (j in 0..degree()) {
                v[k].plus(ctrlPts[span - degree() + j].scl(nders[k][j]))
            }
        }
    }

    // Algorithm 5.1
    fun CurveKnotInsert(t: Float) {
        val low: Int
        var alpha: Float
        val q = LinkedList<Vector3>()
        val span = FindIndexSpan(t)

        for (i in 0..degree()) {
            val tmp = ctrlPts[span - degree() + i]
            q.add(tmp)
        }
        low = span - degree() + 1
        for (i in 0..degree() - 1) {
            alpha = (t - knots[low + i]) / (knots[span + 1 + i] - knots[low + i])
            q[i].scl(1.0f - alpha).plus(q[i + 1].scl(alpha))
        }
        for (i in 0..degree() - 1) {
            if (i != degree() - 1) {
                ctrlPts.removeAt(low + i)
            }
            ctrlPts.add(low + i, q[i])
        }
        knots.add(span + 1, t)
        super.addPts(Vector3.Zero) //dummy corresponding control points

        EvalParamKnotAverages()
        for (i in 1..pts().size() - 1) {
            val u = CurvePoint(prm().get(i))
            pts().remove(i)
            pts().add(i, u)
        }
    }

    //Algorithm 9.1
    private fun evalCtrlPoints() {
        // Evaluate B-spline control points with given points on a curve
        ctrlPts.clear()
        val n = pts().size()
        val indx = IntArray(n)
        val a = Array(n) { DoubleArray(n) }
        val bx = DoubleArray(n)
        val by = DoubleArray(n)
        val bz = DoubleArray(n)
        for (i in 0 until n) {
            val span = FindIndexSpan(prm().get(i))
            val N = FloatArray(order())
            BasisFuncs(span, prm().get(i), N)
            for (j in 0..degree()) {
                a[i][span - degree() + j] = N[j].toDouble()
                //a[span - degree() + j][i] = N[j]; // swap for Fortran
            }
        }
        for (i in 0 until n) {
            bx[i] = pts().get(i).x() as Double
            by[i] = pts().get(i).y() as Double
            bz[i] = pts().get(i).z() as Double
        }
        if (n > 2) {
            MatrixSolvLU.ludcmp(n, a, indx)
            MatrixSolvLU.lubksb(n, a, indx, bx)
            MatrixSolvLU.lubksb(n, a, indx, by)
            MatrixSolvLU.lubksb(n, a, indx, bz)
        }
        for (i in 0 until n) {
            ctrlPts.add(
                    Vector3(
                            bx[i].toFloat(),
                            by[i].toFloat(),
                            bz[i].toFloat()))
        }
    }


    /*
* ctrlPts 1 (point)    : n=1,d=0,o=1,knots={0,1}
* ctrlPts 2 (linear)   : n=2,d=1,o=2,knots={0,0,1,1}
* ctrlPts 3 (quadratic): n=3,d=2,o=3,knots={0,0,0,1,1,1}
* ctrlPts 4 (cubic)    : n=4,d=3,o=4,knots={0,0,0,0,1,1,1,1}
* ctrlPts 5 (quartic)  : n=5,d=4,o=5,knots={0,0,0,0,0,1,1,1,1,1}
* ctrlPts 6 (quintic)  : n=6,d=5,o=6,knots={0,0,0,0,0,0,1,1,1,1,1,1}
* ctrlPts 7 (quintic)  : n=7,d=5,o=6,knots={0,0,0,0,0,0,1/2,1,1,1,1,1,1}
* ctrlPts 8 (quintic)  : n=8,d=5,o=6,knots={0,0,0,0,0,0,1/3,2/3,1,1,1,1,1,1}
* general (quintic)    : n  ,d=5,o=6,knots={...,[1/(n-d),...,(n-o)/(n-d)],...,}
*/
}
*/