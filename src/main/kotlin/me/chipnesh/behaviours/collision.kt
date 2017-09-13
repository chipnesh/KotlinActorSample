package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor
import me.chipnesh.*

class CollisionActor : ActorReference<CollisionCommand>() {

    override val actor = actor<CollisionCommand>(CommonPool) {
        val entities = mutableMapOf<String, Entity>()
        for (command in channel) {
            when (command) {
                is CollisionCommand.CheckCollision -> checkCollision(entities, command.entity)

                is CollisionCommand.StoreEntity -> entities.put(command.entity.id, command.entity)
                is CollisionCommand.DeleteEntity -> entities.remove(command.entity.id)
            }
        }
    }

    private suspend fun checkCollision(entities: MutableMap<String, Entity>, entity: Entity) {
        for ((otherId, other) in entities) {
            val otherPosition = other.position
            val idsNotSame = entity.id != otherId
            val samePosition = distanceBetween(entity.position, otherPosition) == 0.0
            if (idsNotSame && samePosition) {
                printRef.send(PrintCommand("collision detected $other with $entity"))
                handleCollision(entity, other)
            }
        }
    }

    private suspend fun handleCollision(entity: Entity, other: Entity) {
        when(entity) {
            is Entity.Rabbit -> when(other) {
                is Entity.Rabbit -> if (other.gender != entity.gender) spawnRef.send(SpawnCommand.SpawnRabbit())
                is Entity.Wolf -> eatRef.send(EatCommand(entity, other))
            }
            is Entity.Wolf -> when(other) {
                is Entity.Rabbit -> eatRef.send(EatCommand(other, entity))
                is Entity.Wolf -> if (other.gender != entity.gender) spawnRef.send(SpawnCommand.SpawnWolf())
            }
        }
    }

    private fun distanceBetween(it: EntityPosition, other: EntityPosition) = Math.hypot(it.x - other.x.toDouble(), it.y - other.y.toDouble())
}

sealed class CollisionCommand {
    class CheckCollision(val entity: Entity) : CollisionCommand()
    class StoreEntity(val entity: Entity) : CollisionCommand()
    class DeleteEntity(val entity: Entity) : CollisionCommand()
}