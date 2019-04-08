package uk.co.victoriajanedavis.tindercardstack.cardView

enum class Direction {
    Left,
    Right,
    Top,
    Bottom;

    companion object {
        val HORIZONTAL = listOf(
            Left,
            Right
        )
        val VERTICAL = listOf(
            Top,
            Bottom
        )
        val FREEDOM = listOf(Direction.values())
    }
}