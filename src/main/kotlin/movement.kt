import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor

class MoveActor(private val printActor: PrintActor,
                private val collisionActor: CollisionActor) : ActorModel<MoveCommand>() {

    override val actor = actor<MoveCommand>(CommonPool) {
        val positionsState = mutableMapOf<String, EntityPosition>()
        for (command in channel) {
            when (command) {
                is MoveCommand.Left -> changePosition(positionsState, Direction.Left(command))
                is MoveCommand.Right -> changePosition(positionsState, Direction.Right(command))
                is MoveCommand.Up -> changePosition(positionsState, Direction.Up(command))
                is MoveCommand.Down -> changePosition(positionsState, Direction.Down(command))
                is MoveCommand.AddPosition -> positionsState.put(command.entityId, command.position)
                is MoveCommand.DeletePosition -> positionsState.remove(command.entityId)
            }
        }
    }

    private suspend fun changePosition(positions: MutableMap<String, EntityPosition>, direction: Direction) {
        positions.compute(direction.command.entityId) { _, oldPosition ->
            oldPosition?.let {
                moveTo(direction, it)
            }
        }?.let {
            printActor.send(PrintCommand(
                    "moved $it for ${direction.command.steps} steps to ${direction.command.javaClass.simpleName}"
            ))
            collisionActor.send(CollisionCommand.ChangePosition(it))
        }
    }

    private fun moveTo(direction: Direction, old: EntityPosition): EntityPosition = when (direction) {
        is Direction.Left -> old.copy(id = direction.command.entityId, x = old.x - direction.command.steps)
        is Direction.Right -> old.copy(id = direction.command.entityId, x = old.x + direction.command.steps)
        is Direction.Up -> old.copy(id = direction.command.entityId, y = old.y + direction.command.steps)
        is Direction.Down -> old.copy(id = direction.command.entityId, y = old.y - direction.command.steps)
    }
}

private sealed class Direction(val command: MoveCommand) {
    class Left(command: MoveCommand) : Direction(command)
    class Right(command: MoveCommand) : Direction(command)
    class Up(command: MoveCommand) : Direction(command)
    class Down(command: MoveCommand) : Direction(command)
}

sealed class MoveCommand(val entityId: String,
                         val steps: Int) {
    class Left(entityId: String, steps: Int) : MoveCommand(entityId, steps)
    class Right(entityId: String, steps: Int) : MoveCommand(entityId, steps)
    class Up(entityId: String, steps: Int) : MoveCommand(entityId, steps)
    class Down(entityId: String, steps: Int) : MoveCommand(entityId, steps)
    class AddPosition(val position: EntityPosition) : MoveCommand(position.id, 0)
    class DeletePosition(val position: EntityPosition) : MoveCommand(position.id, 0)
}