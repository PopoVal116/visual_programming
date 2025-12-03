import kotlin.random.Random

class Driver : Human {
    constructor(_name: String, _surname: String, _second: String, _sp: Int, _age: Int)
            : super(_name, _surname, _second, -1, _sp, _age)

    override fun move() {
        val dx = Random.nextInt(-speed, speed + 1)

        x += dx
        println("$name водитель переместился на ($dx, 0) в координату ($x, $y)")
    }
}