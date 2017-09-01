package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.channels.ActorJob

abstract class ActorReference<in Command> where Command : Any {
    protected abstract val actor: ActorJob<Command>
    suspend fun send(command: Command) = actor.send(command)
    suspend fun join() = actor.join()
}