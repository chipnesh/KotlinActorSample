package me.chipnesh

import me.chipnesh.behaviours.MoveCommand

data class EntityPosition(val id: String, val x: Int, val y: Int)

sealed class Entity(open val id: String,
                    val position: EntityPosition) {
    data class Rabbit(
            override val id: String,
            val gender: Gender,
            val x: Int = 0,
            val y: Int = 0
    ) : Entity(id, EntityPosition(id, x, y))

    data class Wolf(
            override val id: String,
            val gender: Gender,
            val x: Int = 0,
            val y: Int = 0
    ) : Entity(id, EntityPosition(id, x, y))

    fun left(steps: Int) = MoveCommand.Left(id, steps)
    fun right(steps: Int) = MoveCommand.Right(id, steps)
    fun up(steps: Int) = MoveCommand.Up(id, steps)
    fun down(steps: Int) = MoveCommand.Down(id, steps)
}

sealed class Gender {
    class Male : Gender()
    class Female : Gender()

    override fun toString(): String = javaClass.simpleName
}