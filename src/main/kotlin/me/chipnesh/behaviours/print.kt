package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.actor

class PrintActor : ActorReference<PrintCommand>() {
    override val actor = actor<PrintCommand>(CommonPool) {
        for (command in channel) {
            print(command)
        }
    }

    private fun print(command: PrintCommand) {
        println(command.message)
    }
}

class PrintCommand(val message: String)