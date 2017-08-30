import Entity.Rabbit
import Entity.Wolf
import Gender.Female
import Gender.Male
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

fun main(args: Array<String>) = runBlocking {
    val rabbit = Rabbit(UUID.randomUUID().toString(), Male())
    val wolf = Wolf(UUID.randomUUID().toString(), Female())

    val moveActor = MoveActor()

    moveActor.send(rabbit.left(5))
    moveActor.send(wolf.right(10))
    moveActor.send(rabbit.up(5))

    moveActor.consume {
        println(it)
    }

    moveActor.join()
}
