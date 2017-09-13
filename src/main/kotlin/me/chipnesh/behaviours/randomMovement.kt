package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor
import me.chipnesh.ActorReference
import me.chipnesh.Entity
import me.chipnesh.moveRef
import me.chipnesh.stepSize
import java.util.*

class RandomMoveActor : ActorReference<RandomMoveCommand>() {
    private val random = Random()

    override val actor = actor<RandomMoveCommand>(CommonPool) {
        for (command in channel) {
            moveRef.send(randomMoveCommand(command.entity))
        }
    }

    private fun randomMoveCommand(entity: Entity): MoveCommand = when (random.nextInt(4)) {
        0 -> MoveCommand.Up(entity.id, stepSize)
        1 -> MoveCommand.Down(entity.id, stepSize)
        2 -> MoveCommand.Left(entity.id, stepSize)
        else -> MoveCommand.Right(entity.id, stepSize)
    }
}

class RandomMoveCommand(val entity: Entity)