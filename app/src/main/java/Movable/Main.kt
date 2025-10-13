import kotlin.concurrent.thread

fun main() {
    val humans = arrayOf(
        Human("Алексей", "Иванов", "Сергеевич", 401, 4, 18),
        Human("Мария", "Петрова", "Алексеевна", 402, 3, 19),
        Driver("Боб", "Бобов", "Бобович", 1, 30),
    )

        val simulationTime = 4

    for (t in 1..simulationTime) {
        println("\nTime step: $t")
        val threads = mutableListOf<Thread>()
        for (person in humans) {
            val thread = thread {
                person.move()
            }
            threads.add(thread)
        }
        for (thread in threads) {
            thread.join()
        }
    }
}
