package geoModel

import linearAlgebra.Vector3

class Line() : Parametric() {

    override val ctrlPts = mutableListOf<Vector3>()

    override fun addPts(v: Vector3) {
        if(ctrlPts.size < 2) ctrlPts.add(v); evalPrm()
    }

    override fun addPts(i: Int, v: Vector3) {
        if(ctrlPts.size < 2) ctrlPts.add(i, v); evalPrm()
    }

    override fun removePts(i: Int) {
        if(i != -1) ctrlPts.removeAt(i)
        if(!ctrlPts.isEmpty()) evalPrm()
    }

    override fun evalPrm() {
        prm.clear()
        prm.add(0.0)
        if(ctrlPts.size == 2) prm.add(1.0)
    }

    override fun curvePoint(t: Double): Vector3 {
        return ctrlPts[1] * t + ctrlPts[0] * (1.0 - t)
    }

    override fun curveDers(t: Double, kmax: Int): Array<Vector3> {
        val v = Array(kmax + 1) { Vector3() }
        v[0] = curvePoint(t)
        v[1] = ctrlPts[1]
        for(i in 2..kmax) v[i] = Vector3().zero
        return v
    }



}