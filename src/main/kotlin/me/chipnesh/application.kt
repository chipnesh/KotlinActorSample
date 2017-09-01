package me.chipnesh

import me.chipnesh.Entity.Rabbit
import me.chipnesh.Entity.Wolf
import me.chipnesh.Gender.Female
import me.chipnesh.Gender.Male
import me.chipnesh.behaviours.CollisionActor
import me.chipnesh.behaviours.MoveActor
import me.chipnesh.behaviours.PositionCommand.EntityPlaced
import me.chipnesh.behaviours.PositionActor
import kotlinx.coroutines.experimental.runBlocking
import me.chipnesh.behaviours.PrintActor
import java.util.UUID.randomUUID

fun main(args: Array<String>) = runBlocking {

    val rabbit = Rabbit(randomUUID().toString(), Male(), 5, 5)
    val wolf = Wolf(randomUUID().toString(), Female(), 15, 15)

    val printRef = PrintActor()
    val collisionRef = CollisionActor(printRef)
    val moveRef = MoveActor(printRef, collisionRef)
    val positionRef = PositionActor(moveRef, collisionRef)

    positionRef.send(EntityPlaced(rabbit))
    positionRef.send(EntityPlaced(wolf))

    printRef.join()
}