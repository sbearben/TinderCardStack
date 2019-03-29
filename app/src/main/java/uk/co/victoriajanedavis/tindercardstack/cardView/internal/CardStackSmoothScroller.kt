package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.cardView.CardStackLayoutManager
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.CardStackSmoothScroller.ScrollType.*

class CardStackSmoothScroller(
    private val scrollType: ScrollType,
    private val layoutManager: CardStackLayoutManager
) : RecyclerView.SmoothScroller() {

    private val state = layoutManager.state

    enum class ScrollType {
        AutomaticPositiveSwipe,  // auto right swipe
        AutomaticNegativeSwipe,  // auto left swipe
        AutomaticRewind,  // user pressed the rewind button
        ManualSwipe,  // currently swiping card with finger
        ManualCancel  // user lifts finger when card isn't past the threshold (springs back to centre)
    }

    // RecyclerView will call this method each time it scrolls until it can find the target position/view in the layout.
    // - If we want to trigger a new smooth scroll and cancel the previous one, update the Action object.
    override fun onSeekTargetStep(dx: Int, dy: Int, state: RecyclerView.State, action: Action) {
        Log.d("CardStackSmoothScroller", "onSeekTarget(dx = $dx, dy = $dy)")
        when(scrollType) {
            AutomaticRewind -> {
                //layoutManager.removeAllViews()
                updateAction(action,
                    0,  // Coming up from bottom so no change in X
                    -layoutManager.state.height*2, // twice the height of the card
                    200,
                    DecelerateInterpolator()
                )
            }
            else -> {}
        }
    }

    // Called when the target position/view is laid out. This is the last callback SmoothScroller
    // will receive and it should update the provided {@link Action} to define the scroll
    // details towards the target view.
    // - Action instance that we should update to define final scroll action towards the targetView
    override fun onTargetFound(targetView: View, s: RecyclerView.State, action: Action) {
        val x = targetView.translationX.toInt()
        val y = targetView.translationY.toInt()
        //val setting: AnimationSetting

        when (scrollType) {
            AutomaticPositiveSwipe -> {
                Log.d("CardStackSmoothScroller", "AutomaticPositiveSwipe: dx = ${state.width*2}, dy = ${state.height/4}")
                //val setting = layoutManager.getCardStackSetting().swipeAnimationSetting
                updateAction(action,
                    state.width * 2,
                    state.height / 4,
                    200,
                    AccelerateInterpolator()
                )
            }
            AutomaticNegativeSwipe -> {
                Log.d("CardStackSmoothScroller", "AutomaticNegativeSwipe: dx = ${state.width*2}, dy = ${state.height/4}")
                //val setting = layoutManager.getCardStackSetting().swipeAnimationSetting
                updateAction(action,
                    -state.width * 2,
                    state.height / 4,
                    200,
                    AccelerateInterpolator()
                )
            }
            AutomaticRewind -> {
                Log.d("CardStackSmoothScroller", "AutomaticRewind: dx = ${-x}, dy = ${-y}")
                //setting = layoutManager.getCardStackSetting().rewindAnimationSetting
                updateAction(action,
                    -x,
                    -y,
                    200,
                    DecelerateInterpolator()
                )
            }
            ManualSwipe -> {
                val dx = x * 10
                val dy = y * 10
                Log.d("CardStackSmoothScroller", "ManualSwipe: dx = $dx, dy = $dy")
                //setting = layoutManager.getCardStackSetting().swipeAnimationSetting
                updateAction(action,
                    dx,
                    dy,
                    200,
                    AccelerateInterpolator()
                )
            }
            ManualCancel -> {
                Log.d("CardStackSmoothScroller", "ManualCancel: dx = ${-x}, dy = ${-y}")
                //setting = layoutManager.getCardStackSetting().rewindAnimationSetting
                updateAction(action,
                    -x,
                    -y,
                    200,
                    DecelerateInterpolator()
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
            AutomaticNegativeSwipe -> {}
            //AutomaticRewind -> state.status = Status.RewindAnimating
            AutomaticRewind -> state.status = Status.PrepareRewindAnimation
            ManualSwipe -> {
                state.status = Status.PrepareSwipeAnimation
                //listener.onCardDisappeared(layoutManager.getTopView(), state.topPosition)
            }
            //ManualCancel -> state.status = Status.RewindAnimating
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