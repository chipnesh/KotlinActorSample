package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor
import me.chipnesh.*

class MoveActor : ActorReference<MoveCommand>() {

    override val actor = actor<MoveCommand>(CommonPool) {
        val entities = mutableMapOf<String, Entity>()
        for (command in channel) {
            when (command) {
                is MoveCommand.Left -> changePosition(entities, Direction.Left(command))
                is MoveCommand.Right -> changePosition(entities, Direction.Right(command))
                is MoveCommand.Up -> changePosition(entities, Direction.Up(command))
                is MoveCommand.Down -> changePosition(entities, Direction.Down(command))

                is MoveCommand.StoreEntity -> entities.put(command.entity.id, command.entity)
                is MoveCommand.DeleteEntity -> entities.remove(command.entity.id)
            }
        }
    }

    private suspend fun changePosition(entities: MutableMap<String, Entity>, direction: Direction) {
        updatePosition(entities, direction)?.let {
            collisionRef.send(CollisionCommand.CheckCollision(it))
        }
    }

    private fun updatePosition(entities: MutableMap<String, Entity>, direction: Direction) =
            entities.compute(direction.command.entityId) { _, old -> old?.let {
                val position = moveTo(direction, it)
                when (it) {
                    is Entity.Rabbit -> Entity.Rabbit(it.id, it.gender, position.x, position.y)
                    is Entity.Wolf -> Entity.Wolf(it.id, it.gender, position.x, position.y)
                }
            } }

    private fun moveTo(direction: Direction, old: Entity): EntityPosition = when (direction) {
        is Direction.Left -> old.position.copy(id = direction.command.entityId, x = checkBound(old.position.x, old.position.x - direction.command.steps))
        is Direction.Right -> old.position.copy(id = direction.command.entityId, x = checkBound(old.position.x, old.position.x + direction.command.steps))
        is Direction.Up -> old.position.copy(id = direction.command.entityId, y = checkBound(old.position.y, old.position.y + direction.command.steps))
        is Direction.Down -> old.position.copy(id = direction.command.entityId, y = checkBound(old.position.y, old.position.y - direction.command.steps))
    }

    private fun checkBound(oldPos: Int, newPos: Int): Int = when {
        newPos > fieldSize -> oldPos - (newPos - oldPos)
        newPos < 0 -> -newPos
        else -> newPos
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
    class StoreEntity(val entity: Entity) : MoveCommand(entity.id, 0)
    class DeleteEntity(val entity: Entity) : MoveCommand(entity.id, 0)
}