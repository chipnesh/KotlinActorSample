import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor

class PositionActor(private val moveActor: MoveActor,
                    private val collisionActor: CollisionActor) : ActorModel<PositionCommand>() {

    override val actor = actor<PositionCommand>(CommonPool) {
        val places = mutableMapOf<String, EntityPosition>()
        for (command in channel) {
            when (command) {
                is PositionCommand.EntityPlaced -> placeEntity(places, command)
            }
        }
    }

    private suspend fun placeEntity(places: MutableMap<String, EntityPosition>, command: PositionCommand.EntityPlaced) {
        val place = places.compute(command.entity.id) { _, _ -> command.entity.position }
        place?.let {
            moveActor.send(MoveCommand.AddPosition(place))
            collisionActor.send(CollisionCommand.AddPosition(place))
        }
    }
}

sealed class PositionCommand {
    class EntityPlaced(val entity: Entity) : PositionCommand()
}