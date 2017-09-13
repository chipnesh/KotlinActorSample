package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor
import me.chipnesh.ActorReference
import me.chipnesh.Entity
import me.chipnesh.positionRef
import me.chipnesh.printRef

class EatActor : ActorReference<EatCommand>() {
    override val actor = actor<EatCommand>(CommonPool) {
        var eatenRabbits = 0L
        for (command in channel) {
            eatenRabbits++
            positionRef.send(PositionCommand.RemoveEntity(command.rabbit))
            printRef.send(PrintCommand("Rabbit ${command.rabbit.id} was eaten"))
        }
    }
}

class EatCommand(val rabbit: Entity.Rabbit, val wolf: Entity.Wolf)

