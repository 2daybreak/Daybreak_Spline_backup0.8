package geoModel

import linearAlgebra.Vector3

abstract class Parametric {

    val prm = mutableListOf<Double>()

    abstract val ctrlPts: MutableList<Vector3>

    protected abstract fun evalPrm()

    abstract fun addPts(v: Vector3)

    abstract fun addPts(i: Int, v: Vector3)

    abstract fun removePts(i: Int)

    abstract fun curvePoint(t: Double): Vector3

    abstract fun curveDers(t: Double, kmax: Int): Array<Vector3>

}