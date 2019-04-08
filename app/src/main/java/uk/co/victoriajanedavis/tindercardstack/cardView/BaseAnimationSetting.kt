package uk.co.victoriajanedavis.tindercardstack.cardView

import android.view.animation.Interpolator
import uk.co.victoriajanedavis.tindercardstack.cardView.Direction.*
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.State

abstract class BaseAnimationSetting {

    abstract val direction: Direction
    abstract val duration: Int
    abstract val interpolator: Interpolator

    internal fun getDx(state: State): Int {
        return when(direction) {
            Left -> -state.width * 2
            Right -> state.width * 2
            Top, Bottom -> 0
        }
    }

    internal fun getDy(state: State): Int {
        return when(direction) {
            Left, Right -> state.height / 4
            Top -> -state.height * 2
            Bottom -> state.height * 2
        }
    }
}