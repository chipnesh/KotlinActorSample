package me.chipnesh

import kotlinx.coroutines.experimental.runBlocking
import me.chipnesh.behaviours.*

val fieldSize = 10
val stepSize = 1
val rabbitCount = 5
val wolfCount = 5

val printRef = PrintActor()
val spawnRef = SpawnActor()
val positionRef = PositionActor()
val eatRef = EatActor()
val collisionRef = CollisionActor()
val moveRef = MoveActor()
val randomMoveRef = RandomMoveActor()

fun main(args: Array<String>) = runBlocking {
    (1..rabbitCount).forEachIndexed { index, _ ->
        spawn(index, SpawnCommand.SpawnRabbit())
    }
    (1..wolfCount).forEachIndexed { index, _ ->
        spawn(index, SpawnCommand.SpawnRabbit())
    }
    printRef.join()
}

private suspend fun spawn(index: Int, spawnCommand: SpawnCommand) {
    spawnCommand.gender = if (index.isEven()) Gender.Female() else Gender.Male()
    spawnRef.send(spawnCommand)
}

private fun Int.isEven() = this and 1 == 0
