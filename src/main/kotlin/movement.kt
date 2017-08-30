import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach

class MoveActor : ActorModel<MoveCommand, EntityPosition> {

    private val response = Channel<EntityPosition>()

    private val actor = actor<MoveCommand>(CommonPool) {
        val positionsState = mutableMapOf<String, EntityPosition>()
        for (command in channel) {
            when (command) {
                is MoveCommand.Left -> respond(changePosition(positionsState, Direction.Left(command)))
                is MoveCommand.Right -> respond(changePosition(positionsState, Direction.Right(command)))
                is MoveCommand.Up -> respond(changePosition(positionsState, Direction.Up(command)))
                is MoveCommand.Down -> respond(changePosition(positionsState, Direction.Down(command)))
            }
        }
    }

    private fun respond(position: EntityPosition) = async(CommonPool) { response.send(position) }

    suspend override fun consume(block: (EntityPosition) -> Unit) = response.consumeEach(block)

    override suspend fun send(command: MoveCommand) = actor.send(command)

    override suspend fun join() = actor.join()

    private fun changePosition(positions: MutableMap<String, EntityPosition>, direction: Direction) =
            positions.computeIfPresent(direction.command.animalName) { _, oldPosition ->
                moveTo(direction, oldPosition)
            } ?: moveTo(direction, EntityPosition(direction.command.animalName, 0, 0))

    private fun moveTo(direction: Direction, old: EntityPosition): EntityPosition = when (direction) {
        is Direction.Left -> old.copy(name = direction.command.animalName, x = 0, y = -direction.command.steps)
        is Direction.Right -> old.copy(name = direction.command.animalName, x = 0, y = direction.command.steps)
        is Direction.Up -> old.copy(name = direction.command.animalName, x = direction.command.steps, y = 0)
        is Direction.Down -> old.copy(name = direction.command.animalName, x = -direction.command.steps, y = 0)
    }
}

private sealed class Direction(val command: MoveCommand) {
    class Left(command: MoveCommand) : Direction(command)
    class Right(command: MoveCommand) : Direction(command)
    class Up(command: MoveCommand) : Direction(command)
    class Down(command: MoveCommand) : Direction(command)
}

sealed class MoveCommand(val animalName: String,
                         val steps: Int) {
    class Left(name: String, steps: Int) : MoveCommand(name, steps)
    class Right(name: String, steps: Int) : MoveCommand(name, steps)
    class Up(name: String, steps: Int) : MoveCommand(name, steps)
    class Down(name: String, steps: Int) : MoveCommand(name, steps)
}