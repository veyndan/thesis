import org.apache.commons.math3.ml.distance.EuclideanDistance
import kotlin.random.Random

open class Bound(val value: Double, private val range: ClosedFloatingPointRange<Double>) {

    init {
        require(value in range) { "$value not in $range" }
    }
}

fun euclideanDistance(a: List<Bound>, b: List<Bound>): Double =
    EuclideanDistance().compute(a.map(Bound::value).toDoubleArray(), b.map(Bound::value).toDoubleArray())

val random = if (DEBUG) Random(0) else Random
