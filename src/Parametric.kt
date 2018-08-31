package geoModel

import linearAlgebra.Vector3

abstract class Parametric {

    public abstract val prm: MutableList<Double>

    public abstract val ctrlPts: MutableList<Vector3>

    public abstract fun addPts(v: Vector3)

    public abstract fun addPts(i: Int, v: Vector3)

    public abstract fun removePts(i: Int)
    
    public operator fun invoke(t: Double): Vector3 { return curvePoint(t) }
    
    public operator fun invoke(kmax: Int, t: Double): Array<Vector3> { return curveDers(kmax, t) }

    protected abstract fun evalPrm(p: MutableList<Vector3>)
    
    protected abstract fun curvePoint(t: Double): Vector3

    protected abstract fun curveDers(kmax: Int, t: Double): Array<Vector3>

}
