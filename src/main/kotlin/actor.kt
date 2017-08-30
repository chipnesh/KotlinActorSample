interface ActorModel<in Command, out Response> {
    suspend fun send(command: Command)
    suspend fun consume(block: Response.() -> Unit)
    suspend fun join()
}