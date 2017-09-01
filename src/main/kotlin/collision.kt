import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor

class CollisionActor(private val printActor: PrintActor) : ActorModel<CollisionCommand>() {

    override val actor = actor<CollisionCommand>(CommonPool) {
        val positionsState = mutableMapOf<String, EntityPosition>()
        for (command in channel) {
            when (command) {
                is CollisionCommand.CheckCollision -> checkCollision(positionsState, command.position)
                is CollisionCommand.AddPosition -> storePosition(positionsState, command.position)
                is CollisionCommand.ChangePosition -> storePosition(positionsState, command.position)
                is CollisionCommand.DeletePosition -> positionsState.remove(command.position.id)
            }
        }
    }

    private suspend fun checkCollision(positions: MutableMap<String, EntityPosition>, position: EntityPosition) {
        for ((otherId, otherPosition) in positions) {
            val idsNotSame = position.id != otherId
            val distanceLowerThanFive = distanceBetween(position, otherPosition) == 0.0
            if (idsNotSame && distanceLowerThanFive) {
                printActor.send(PrintCommand("collision detected ${position.id} with $otherId"))
            }
        }
    }

    private fun distanceBetween(it: EntityPosition, other: EntityPosition) = Math.hypot(it.x - other.x.toDouble(), it.y - other.y.toDouble())

    private suspend fun storePosition(positionsState: MutableMap<String, EntityPosition>, position: EntityPosition) {
        positionsState.put(position.id, position)
        checkCollision(positionsState, position)
    }
}

sealed class CollisionCommand {
    class CheckCollision(val position: EntityPosition) : CollisionCommand()
    class AddPosition(val position: EntityPosition) : CollisionCommand()
    class ChangePosition(val position: EntityPosition) : CollisionCommand()
    class DeletePosition(val position: EntityPosition) : CollisionCommand()
}