package geoModel

import linearAlgebra.Vector3

abstract class Parametric {

    val prm = mutableListOf<Double>()

    protected abstract fun evalPrm()

    abstract fun curvePoint(t: Double): Vector3

    abstract fun curveDers(t: Double, kmax: Int): Array<Vector3>

}