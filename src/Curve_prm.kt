open class Curve_prm: ICurve {

    protected val prm = mutableListOf<Double>()

    protected val pts = mutableListOf<Vector3>()

    override fun addPts(v: Vector3) {
        pts.add(v)
        evalPrm()
    }

    override fun removePts(v: Vector3) {
        pts.remove(v)
        if(pts.isEmpty()==false) evalPrm()
    }

    private fun evalPrm() {
        prm.clear()
        var sum = 0.toDouble()
        prm.add(sum)
        //Chord length method
        for(i in 1 until pts.count())
        {
            var del = pts[i] - pts[i - 1]
            sum += del.length
        }
        for(i in 1 until pts.count())
        {
            var del = pts[i] - pts[i - 1]
            prm.add(prm[i - 1] + del.length / sum)
        }
    }
}