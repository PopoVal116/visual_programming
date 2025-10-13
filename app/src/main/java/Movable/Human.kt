import kotlin.random.Random

open class Human : Movable {

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

    override var speed: Int = 1
        get() = field
        set(value) {
            if (value in 0..10) {
                field = value
            } else {
                println("Скорость человека странная...")
            }
        }

    override var x: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override var y: Int = 0
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
        x = 0
        y = 0
        println("Мы создали человека с именем: $name ")
    }


    override fun move() {
        val deltaX = Random.nextInt(-speed, speed + 1)
        val deltaY = Random.nextInt(-speed, speed + 1)
        x += deltaX
        y += deltaY
        println("$name переместился на $deltaX, $deltaY в координату $x, $y")
    }
}
