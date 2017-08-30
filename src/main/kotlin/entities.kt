data class EntityPosition(val name: String, val x: Int, val y: Int)

sealed class Entity(private val id: String, private val position: EntityPosition) {

    data class Rabbit(
            private val id: String,
            private val gender: Gender,
            private val position: EntityPosition = EntityPosition(id, 0, 0)
    ) : Entity(id, position)

    data class Wolf(
            private val id: String,
            private val gender: Gender,
            private val position: EntityPosition = EntityPosition(id, 0, 0)
    ) : Entity(id, position)

    fun left(steps: Int) = MoveCommand.Left(id, steps)
    fun right(steps: Int) = MoveCommand.Right(id, steps)
    fun up(steps: Int) = MoveCommand.Up(id, steps)
    fun down(steps: Int) = MoveCommand.Down(id, steps)
}

sealed class Gender {
    class Male : Gender()
    class Female : Gender()
}