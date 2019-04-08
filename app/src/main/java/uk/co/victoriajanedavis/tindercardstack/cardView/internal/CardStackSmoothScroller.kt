package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.cardView.CardStackLayoutManager
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.CardStackSmoothScroller.ScrollType.*

internal class CardStackSmoothScroller(
    private val scrollType: ScrollType,
    layoutManager: CardStackLayoutManager
) : RecyclerView.SmoothScroller() {

    private val state = layoutManager.state
    private val settings = layoutManager.settings

    enum class ScrollType {
        AutomaticPositiveSwipe,  // auto right swipe
        AutomaticNegativeSwipe,  // auto left swipe
        AutomaticRewind,  // user pressed the rewind button
        ManualSwipe,  // currently swiping card with finger
        ManualCancel  // user lifts finger when card isn't past the threshold (springs back to centre)
    }

    override fun onSeekTargetStep(dx: Int, dy: Int, s: RecyclerView.State, action: Action) {
        // Used to essentially trigger scrollBy and subsequently update() to layout the target view coming from a rewind
        when(scrollType) {
            AutomaticRewind -> {
                val animationSetting = settings.rewindAnimation
                updateAction(action,  // dx and dy here are used to set the initial position of the card coming in from a rewind
                    animationSetting.getDx(state),
                    animationSetting.getDy(state),
                    animationSetting.duration,
                    animationSetting.interpolator
                )
            }
            else -> {}
        }
    }

    override fun onTargetFound(targetView: View, s: RecyclerView.State, action: Action) {
        val x = targetView.translationX.toInt()
        val y = targetView.translationY.toInt()

        when (scrollType) {
            AutomaticPositiveSwipe -> {
                Log.d("CardStackSmoothScroller", "AutomaticPositiveSwipe: dx = ${state.width*2}, dy = ${state.height/4}")
                val animationSetting = settings.positiveSwipeAnimation
                updateAction(action,
                    animationSetting.getDx(state),
                    animationSetting.getDy(state),
                    animationSetting.duration,
                    animationSetting.interpolator
                )
            }
            AutomaticNegativeSwipe -> {
                Log.d("CardStackSmoothScroller", "AutomaticNegativeSwipe: dx = ${state.width*2}, dy = ${state.height/4}")
                val animationSetting = settings.negativeSwipeAnimation
                updateAction(action,
                    animationSetting.getDx(state),
                    animationSetting.getDy(state),
                    animationSetting.duration,
                    animationSetting.interpolator
                )
            }
            AutomaticRewind -> {
                Log.d("CardStackSmoothScroller", "AutomaticRewind: dx = ${-x}, dy = ${-y}")
                val animationSetting = settings.rewindAnimation
                updateAction(action,
                    -x,
                    -y,
                    animationSetting.duration,
                    animationSetting.interpolator
                )
            }
            ManualSwipe -> {
                Log.d("CardStackSmoothScroller", "ManualSwipe")
                val animationSetting = settings.positiveSwipeAnimation
                updateAction(action,
                    x * 10,
                    y * 10,
                    animationSetting.duration,
                    animationSetting.interpolator
                )
            }
            ManualCancel -> {
                Log.d("CardStackSmoothScroller", "ManualCancel: dx = ${-x}, dy = ${-y}")
                val animationSetting = settings.rewindAnimation
                updateAction(action,
                    -x,
                    -y,
                    animationSetting.duration,
                    animationSetting.interpolator
                )
            }
        }
    }

    override fun onStart() {
        when (scrollType) {
            AutomaticPositiveSwipe -> {
                state.status = Status.PrepareSwipeAnimation
                //listener.onCardDisappeared(layoutManager.getTopView(), state.topPosition)
            }
            AutomaticNegativeSwipe -> {
                state.status = Status.PrepareSwipeAnimation
                //listener.onCardDisappeared(layoutManager.getTopView(), state.topPosition)
            }
            AutomaticRewind -> state.status = Status.PrepareRewindAnimation
            ManualSwipe -> {
                state.status = Status.PrepareSwipeAnimation
                //listener.onCardDisappeared(layoutManager.getTopView(), state.topPosition)
            }
            ManualCancel -> state.status = Status.RewindAnimating
        }
    }

    override fun onStop() {
        when (scrollType) {
            AutomaticPositiveSwipe -> {}
            AutomaticNegativeSwipe -> {}
            AutomaticRewind -> {
                //listener.onCardRewound()
                //listener.onCardAppeared(layoutManager.getTopView(), state.topPosition)
            }
            ManualSwipe -> {}
            ManualCancel -> {} //listener.onCardCanceled()
        }
    }

    /*
     * Why are we reversing dx and dy? Because when drawing in Android (0,0) is the top-left corner of the screen
     * - from what I can tell action.update appears to treat (0,0) as the bottom-right corner
     */
    private fun updateAction(action: Action, dx: Int, dy: Int, duration: Int, interpolator: Interpolator) {
        action.update(
            -dx,
            -dy,
            duration,
            interpolator
        )
    }
}