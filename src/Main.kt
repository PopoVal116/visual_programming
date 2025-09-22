import kotlin.random.Random
import kotlin.concurrent.thread

open class Human {

     public var name: String = ""
        get() = field
        set(value) {
            field = value
        }
    public var surname: String = ""
        get() = field
        set(value) {
            field = value
        }
    public var second_name: String = ""
        get() = field
        set(value) {
            field = value
        }
    var group_number: Int = -1
        get() = field
        set(value) {
            field = value
        }
    public var age: Int = 0
        get() = field
        set(value) {
            if (value in 0..100) {
                field = value
            } else {
                println("Возраст человека странный...")
            }
        }
    public var speed: Int = 1
        get() = field
        set(value) {
            if (value in 0..10) {
                field = value
            } else {
                println("Скорость человека странная...")
            }
        }
    public var x: Int = 0
        get() = field
        set(value) {
            field = value
        }
    public var y: Int = 0
        get() = field
        set(value) {
            field = value
        }

    constructor (_name: String, _surname: String, _second: String, _gn: Int, _sp: Int, _age: Int) {
        name = _name
        surname = _surname
        second_name = _second
        group_number = _gn
        speed = _sp
        age = _age
        println("Мы создали человека с именем: $name ")
    }

    open fun move() {
        val deltaX = Random.nextInt(-speed, speed + 1)
        val deltaY = Random.nextInt(-speed, speed + 1)
        x += deltaX
        y += deltaY
        println("$name переместился на $deltaX, $deltaY в координату $x, $y")
    }
}

class Driver: Human {
    constructor(_name: String, _surname: String, _second: String, _sp: Int, _age: Int)
            : super(_name, _surname, _second, -1, _sp, _age)

    override fun move() {
        val dx = Random.nextInt(-speed, speed+1)
        x += dx
        println("$name водитель переместился на ($dx, 0) в координату ($x, $y)")
    }
    }

fun main() {
    val humans = arrayOf(
        Human("Алексей", "Иванов", "Сергеевич", 401, 4, 18),
        Human("Мария", "Петрова", "Алексеевна", 402, 3, 19),
        Driver("Боб", "Бобов", "Бобович", 1, 30)
    )

    val simulationTime = 4

    for (t in 1..simulationTime) {
        println("\nTime step: $t")
        val threads = mutableListOf<Thread>()
        for (person in humans) {
            val thread = thread{
                person.move()
            }
            threads.add(thread)
        }
        for (thread in threads) {
            thread.start()
            thread.join()
        }
    }
}