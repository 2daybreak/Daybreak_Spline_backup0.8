fun main(args: Array<String>) {
    println("Hello World")

    val curve = mutableListOf<Bspline>()
    curve.add(Bspline(3))
    curve[0].addPts(Vector3(3.0, 3.0, 0.0))
    curve[0].addPts(Vector3(1.0, 1.0, 0.0))
    curve[0].addPts(Vector3(0.5, 0.5, 0.0))
    curve[0].addPts(Vector3().zero)
    println("curvePoint(t=0.5) : x = ${curve[0].curvePoint(0.5).x}, y = ${curve[0].curvePoint(0.5).y}")
    println("curveDers1(t=0.5) : x = ${curve[0].curveDers(0.5,1)[1].x}, y = ${curve[0].curveDers(0.5,1)[1].y}")

    val u = Vector3().unitZ
    println("u[2]=${u[2]}")
    val mm = Matrix3().identity
    println("det=${mm.determinant}")
    val nn = Matrix(3).identity
    println("det=${nn.determinant}")
    val ll = Matrix(mm.m)
    println("det=${ll.determinant}")

}