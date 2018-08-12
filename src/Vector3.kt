import kotlin.math.sqrt

class Vector3(val x: Double, val y: Double, val z: Double) {

    val zero   get() = Vector3(0.0, 0.0, 0.0)
    val unitX  get() = Vector3(1.0, 0.0, 0.0)
    val unitY  get() = Vector3(0.0, 1.0, 0.0)
    val unitZ  get() = Vector3(0.0, 0.0, 1.0)
    val length get() = sqrt(x * x + y * y + z * z)

    constructor(): this(0.0, 0.0, 0.0)
    constructor(v: Vector3): this(v.x, v.y, v.z)

    operator fun get(index: Int): Double {
        return when (index) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IndexOutOfBoundsException("Index out of bounds at $index")
        }
    }
    operator fun unaryPlus()       = Vector3(+x, +y, +z)
    operator fun unaryMinus()      = Vector3(-x, -y, -z)
    operator fun times(d: Double)  = Vector3(x * d, y * d, z * d)
    operator fun div  (d: Double)  = Vector3(x / d, y / d, z / d)
    operator fun plus (v: Vector3) = Vector3(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector3) = Vector3(x - v.x, y - v.y, z - v.z)
    fun dot(v: Vector3)  = x * v.x + y * v.y + z * v.z
    fun cross(v:Vector3) = Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
}