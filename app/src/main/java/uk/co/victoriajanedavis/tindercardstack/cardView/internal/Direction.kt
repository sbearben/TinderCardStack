package uk.co.victoriajanedavis.tindercardstack.cardView.internal

enum class Direction {
    Left,
    Right,
    Top,
    Bottom;

    companion object {
        val HORIZONTAL = listOf(Direction.Left, Direction.Right)
        val VERTICAL = listOf(Direction.Top, Direction.Bottom)
        val FREEDOM = listOf(Direction.values())
    }
}