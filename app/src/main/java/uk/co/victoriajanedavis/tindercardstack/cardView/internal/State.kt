package uk.co.victoriajanedavis.tindercardstack.cardView.internal

class State {

    var topPosition = 0  // Represents the adapter position of the card that's on top/currently displayed

    var status = Status.Idle

    var width = 0

    var height = 0

    var dx = 0

    var dy = 0

    var proportion = 0f


    fun getDirection(): Direction {
        return if (Math.abs(dy) < Math.abs(dx)) {
            if (dx < 0.0f) {
                Direction.Left
            } else {
                Direction.Right
            }
        } else {
            if (dy < 0.0f) {
                Direction.Top
            } else {
                Direction.Bottom
            }
        }
    }

    fun getRatio(): Float {
        val absDx = Math.abs(dx)
        val absDy = Math.abs(dy)

        val ratio = if (absDx < absDy) {
            absDy / (height / 2.0f)
        } else {
            absDx / (width / 2.0f)
        }

        return Math.min(ratio, 1.0f)
    }

    fun setFieldsToIdle() {
        status = Status.Idle
        //targetPosition = RecyclerView.NO_POSITION
    }
}