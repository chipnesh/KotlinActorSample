package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor
import me.chipnesh.*

class PositionActor : ActorReference<PositionCommand>() {

    override val actor = actor<PositionCommand>(CommonPool) {
        val entities = mutableMapOf<String, Entity>()
        for (command in channel) {
            when (command) {
                is PositionCommand.PlaceEntity -> placeEntity(entities, command)
                is PositionCommand.RemoveEntity -> removeEntity(entities, command)
            }
        }
    }

    private suspend fun removeEntity(entities: MutableMap<String, Entity>, command: PositionCommand.RemoveEntity) {
        entities.remove(command.entity.id)
        moveRef.send(MoveCommand.DeleteEntity(command.entity))
        collisionRef.send(CollisionCommand.DeleteEntity(command.entity))
    }

    private suspend fun placeEntity(positions: MutableMap<String, Entity>, command: PositionCommand.PlaceEntity) {
        placePosition(positions, command)?.let {
            printRef.send(PrintCommand("Entity $it placed"))
            moveRef.send(MoveCommand.StoreEntity(it))
            collisionRef.send(CollisionCommand.StoreEntity(it))
        }
    }

    private fun placePosition(places: MutableMap<String, Entity>, command: PositionCommand.PlaceEntity) =
            places.compute(command.entity.id) { _, _ -> command.entity }
}

sealed class PositionCommand {
    class PlaceEntity(val entity: Entity) : PositionCommand()
    class RemoveEntity(val entity: Entity) : PositionCommand()
}