import kotlin.random.Random

class Human {

    var name: String = ""
        get() = field
        set(value) {
            field = value
        }
    var surname: String = ""
        get() = field
        set(value) {
            field = value
        }
    var second_name: String = ""
        get() = field
        set(value) {
            field = value
        }
    var group_number: Int = -1
        get() = field
        set(value) {
            field = value
        }
    var age: Int = 0
        get() = field
        set(value) {
            if (value in 0..100) {
                field = value
            } else {
                println("Возраст человека странный...")
            }
        }
    var speed: Int = 1
        get() = field
        set(value) {
            if (value in 0..10) {
                field = value
            } else {
                println("Скорость человека странная...")
            }
        }
    var x: Int = 0
        get() = field
        set(value) {
            field = value
        }
    var y: Int = 0
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

    fun move() {
        val deltaX = Random.nextInt(-speed, speed + 1)
        val deltaY = Random.nextInt(-speed, speed + 1)
        x += deltaX
        y += deltaY
        println("$name переместился на $deltaX, $deltaY в координату $x, $y")
    }
}

fun main() {
    val humans = arrayOf(
        Human("Алексей", "Иванов", "Сергеевич", 401, 4, 18),
        Human("Мария", "Петрова", "Алексеевна", 402, 3, 19),
        Human("Иван", "Сидоров", "Дмитриевич", 403, 2, 20),
        Human("Анна", "Смирнова", "Павловна", 404, 5, 21),
        Human("Дмитрий", "Кузнецов", "Иванович", 405, 3, 22),
        Human("Екатерина", "Попова", "Владимировна", 406, 4, 23),
        Human("Сергей", "Васильев", "Александрович", 407, 2, 18),
        Human("Ольга", "Морозова", "Сергеевна", 408, 3, 19),
        Human("Павел", "Новиков", "Олегович", 409, 4, 20),
        Human("Юлия", "Козлова", "Дмитриевна", 410, 5, 21),
        Human("Никита", "Соколов", "Викторович", 411, 3, 22),
        Human("Татьяна", "Лебедева", "Андреевна", 412, 2, 23),
        Human("Владимир", "Орлов", "Михайлович", 413, 4, 18),
        Human("Елена", "Фёдорова", "Игоревна", 414, 3, 19),
        Human("Михаил", "Белов", "Сергеевич", 415, 5, 20),
        Human("Арина", "Волкова", "Алексеевна", 416, 2, 21),
        Human("Артём", "Макаров", "Дмитриевич", 417, 4, 22),
        Human("Светлана", "Зайцева", "Павловна", 418, 3, 23),
        Human("Роман", "Григорьев", "Иванович", 419, 2, 18),
        Human("Ксения", "Егорова", "Владимировна", 420, 5, 19),
        Human("Игорь", "Павлов", "Александрович", 421, 3, 20),
        Human("Вера", "Соколова", "Сергеевна", 422, 4, 21)
    )

    val simulationTime = 4

    for (t in 1..simulationTime) {
        println("\nTime step: $t")
        for (human in humans) {
            human.move()
        }
    }
}