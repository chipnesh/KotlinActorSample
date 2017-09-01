import Entity.Rabbit
import Entity.Wolf
import Gender.Female
import Gender.Male
import PositionCommand.EntityPlaced
import kotlinx.coroutines.experimental.runBlocking
import java.util.UUID.randomUUID

fun main(args: Array<String>) = runBlocking {

    val rabbit = Rabbit(randomUUID().toString(), Male(), 5, 5)
    val wolf = Wolf(randomUUID().toString(), Female(), 15, 15)

    val printActor = PrintActor()
    val collisionActor = CollisionActor(printActor)
    val moveActor = MoveActor(printActor, collisionActor)
    val positionActor = PositionActor(moveActor, collisionActor)

    positionActor.send(EntityPlaced(rabbit))
    positionActor.send(EntityPlaced(wolf))

    moveActor.send(rabbit.right(5))
    moveActor.send(rabbit.up(5))
    moveActor.send(wolf.down(5))
    moveActor.send(wolf.left(5))
    // first collision
    moveActor.send(wolf.left(5))
    moveActor.send(rabbit.left(5))
    // second collision

    printActor.join()
}