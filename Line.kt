package geoModel

import linearAlgebra.Vector3

class Line : Parametric() {

    override var degree = 1
    override val prm = mutableListOf<Double>()
    override val ctrlPts = mutableListOf<Vector3>()
    override var endDers = arrayOf(Vector3(), Vector3())

    override fun addPts(v: Vector3) {
        if(ctrlPts.size < 2) ctrlPts.add(v); properties(ctrlPts)
    }

    override fun addPts(i: Int, v: Vector3) {
        if(ctrlPts.size < 2) ctrlPts.add(i, v); properties(ctrlPts)
    }

    override fun removePts(i: Int) {
        if(i != -1) ctrlPts.removeAt(i)
        if(!ctrlPts.isEmpty()) properties(ctrlPts)
    }

    override fun properties(p: MutableList<Vector3>) {
        prm.clear()
        prm.add(0.0)
        if(ctrlPts.size == 2) prm.add(1.0)
    }

    override fun curvePoint(t: Double): Vector3 {
        return ctrlPts[1] * t + ctrlPts[0] * (1.0 - t)
    }

    override fun curveDers(kmax: Int, t: Double): Array<Vector3> {
        val v = Array(kmax + 1) { Vector3() }
        v[0] = curvePoint(t)
        v[1] = ctrlPts[1]
        for(i in 2..kmax) v[i] = Vector3().zero
        return v
    }

    override fun closestPoint(v: Vector3): Vector3 {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun curveKnotInsert(t: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}