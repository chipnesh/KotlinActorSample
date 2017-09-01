import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.ActorJob
import kotlinx.coroutines.experimental.channels.actor

class PrintActor : ActorModel<PrintCommand>() {
    override val actor: ActorJob<PrintCommand> = actor(CommonPool) {
            for (command in channel) {
                print(command)
            }
        }

    private fun print(command: PrintCommand) {
        println(command.message)
    }
}

class PrintCommand(val message: String)