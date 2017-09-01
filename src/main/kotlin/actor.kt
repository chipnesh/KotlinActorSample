import kotlinx.coroutines.experimental.channels.ActorJob

abstract class ActorModel<in Command> where Command : Any {
    protected abstract val actor: ActorJob<Command>
    suspend fun send(command: Command) = actor.send(command)
    suspend fun join() = actor.join()
}