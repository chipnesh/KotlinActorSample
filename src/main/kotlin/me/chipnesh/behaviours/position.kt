package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor
import me.chipnesh.Entity
import me.chipnesh.EntityPosition

class PositionActor(private val moveRef: ActorReference<MoveCommand>,
                    private val collisionRef: ActorReference<CollisionCommand>) : ActorReference<PositionCommand>() {

    override val actor = actor<PositionCommand>(CommonPool) {
        val positionState = mutableMapOf<String, EntityPosition>()
        for (command in channel) {
            when (command) {
                is PositionCommand.EntityPlaced -> placeEntity(positionState, command)
            }
        }
    }

    private suspend fun placeEntity(positions: MutableMap<String, EntityPosition>, command: PositionCommand.EntityPlaced) {
        val placedPosition = placePosition(positions, command)
        placedPosition?.let {
            moveRef.send(MoveCommand.AddPosition(placedPosition))
            collisionRef.send(CollisionCommand.AddPosition(placedPosition))
        }
    }

    private fun placePosition(places: MutableMap<String, EntityPosition>, command: PositionCommand.EntityPlaced) =
            places.compute(command.entity.id) { _, _ -> command.entity.position }
}

sealed class PositionCommand {
    class EntityPlaced(val entity: Entity) : PositionCommand()
}