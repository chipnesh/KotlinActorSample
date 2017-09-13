package me.chipnesh.behaviours

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import me.chipnesh.*
import java.util.*
import java.util.concurrent.TimeUnit

class SpawnActor : ActorReference<SpawnCommand>() {

    private val random = Random()

    override val actor = actor<SpawnCommand>(CommonPool) {
        for (command in channel) {
            when (command) {
                is SpawnCommand.SpawnRabbit -> {
                    val rabbit = newRabbit(command.x, command.y, command.gender)
                    place(rabbit)
                    launchRandomMovement(rabbit)
                }
                is SpawnCommand.SpawnWolf -> {
                    val wolf = newWolf(command.x, command.y, command.gender)
                    place(wolf)
                    launchRandomMovement(wolf)
                }
            }
        }
    }

    private fun launchRandomMovement(entity: Entity) {
        async(CommonPool) {
            while (true) {
                randomMoveRef.send(RandomMoveCommand(entity))
                delay(5, TimeUnit.SECONDS)
            }
        }
    }

    private suspend fun place(entity: Entity) {
        positionRef.send(PositionCommand.PlaceEntity(entity))
    }

    private fun newWolf(x: Int, y: Int, gender: Gender?): Entity.Wolf {
        val id = UUID.randomUUID().toString()
        val newX = if (x == -1) randomCoordinate() else x
        val newY = if (y == -1) randomCoordinate() else y
        val newGender = gender ?: randomGender()
        return Entity.Wolf(id, newGender, newX, newY)
    }

    private fun randomCoordinate(): Int = random.nextInt(fieldSize)

    private fun newRabbit(x: Int, y: Int, gender: Gender?): Entity.Rabbit {
        val id = UUID.randomUUID().toString()
        val newX = if (x == -1) randomCoordinate() else x
        val newY = if (y == -1) randomCoordinate() else y
        val newGender = gender ?: randomGender()
        return Entity.Rabbit(id, newGender, newX, newY)
    }

    private fun randomGender() = when(random.nextBoolean()) {
        true -> Gender.Female()
        false -> Gender.Male()
    }

}
sealed class SpawnCommand(val x: Int, val y: Int, var gender: Gender? = null) {
    class SpawnRabbit(x: Int = -1, y: Int = -1, gender: Gender? = null) : SpawnCommand(x, y, gender)
    class SpawnWolf(x: Int = -1, y: Int = -1, gender: Gender? = null) : SpawnCommand(x, y, gender)
}