package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.cardView.CardStackLayoutManager

class CardStackFlingListener(
    private var recyclerView: RecyclerView
) : RecyclerView.OnFlingListener() {

    private var gravityScroller = initializeGravityScroller(recyclerView)

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        private var hasScrolled = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && hasScrolled) {
                hasScrolled = false
                Log.d("CardStackFlingListener", "RecyclerView.SCROLL_STATE_IDLE && hasScrolled")
                snapFromFling(recyclerView.layoutManager!!, 0, 0)
                //snapToTargetExistingView()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx != 0 || dy != 0) {
                hasScrolled = true
            }
        }
    }

    init {
        initRecyclerViewAndCallbacks(recyclerView)
    }

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        recyclerView.adapter ?: return false
        val layoutManager = recyclerView.layoutManager ?: return false
        val minFlingVelocity = recyclerView.minFlingVelocity

        Log.d("CardStackFlingListener", "onFling: velocityY=$velocityY, velocityX=$velocityX, minFlingVelocity=$minFlingVelocity")

        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity) &&
                snapFromFling(layoutManager, velocityX, velocityY)
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        if (this.recyclerView === recyclerView) {
            return  // nothing to do
        }
        initRecyclerViewAndCallbacks(recyclerView)
    }

    /*
    // Estimates the x, y distances from fling velocities
    fun calculateScrollDistance(velocityX: Int, velocityY: Int): IntArray {
        val outDist = IntArray(2)
        gravityScroller.fling(
            0, 0, velocityX, velocityY,
            Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE
        )
        outDist[0] = gravityScroller.finalX
        outDist[1] = gravityScroller.finalY
        return outDist
    }
    */

    private fun snapFromFling(lm: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Boolean {
        Log.d("CardStackFlingListener", "snapFromFling(before)")
        findSnapView(lm)?.let { targetView ->
            Log.d("CardStackFlingListener", "snapFromFling(after)")
            val layoutManager = lm as CardStackLayoutManager
            val x = targetView.translationX.toInt()
            val y = targetView.translationY.toInt()

            Log.d("CardStackFlingListener", "snapFromFling(after) x: $x, y: $y")

            if (x != 0 || y != 0) {
                val state = layoutManager.state

                val horizontal = Math.abs(x) / targetView.width.toFloat()
                val vertical = Math.abs(y) / targetView.height.toFloat()

                if (horizontal > SWIPE_THRESHOLD || vertical > SWIPE_THRESHOLD) {
                    if (Direction.HORIZONTAL.contains(state.getDirection())) {
                        val scroller =
                            CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualSwipe, layoutManager)
                        scroller.targetPosition = state.topPosition
                        Log.d("CardStackFlingListener", "startSmoothScroller(ManualSwipe)")
                        layoutManager.startSmoothScroll(scroller)
                    } else {
                        val scroller =
                            CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualCancel, layoutManager)
                        scroller.targetPosition = state.topPosition
                        Log.d("CardStackFlingListener", "startSmoothScroller(ManualCancel)")
                        layoutManager.startSmoothScroll(scroller)
                    }
                } else {
                    val scroller =
                        CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualCancel, layoutManager)
                    scroller.targetPosition = state.topPosition
                    Log.d("CardStackFlingListener", "startSmoothScroller(ManualCancel)")
                    layoutManager.startSmoothScroll(scroller)
                }
                return true
            }
        }
        return false
    }

    /*
    private fun snapToTargetExistingView() {
        val layoutManager = recyclerView.layoutManager ?: return
        val snapView = findSnapView(layoutManager) ?: return

        /*
        val snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView)

        if (snapDistance!![0] != 0 || snapDistance!![1] != 0) {
            recyclerView.smoothScrollBy(snapDistance!![0], snapDistance!![1])
        }
        */
    }
    */

    private fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return if (layoutManager is CardStackLayoutManager) {
            layoutManager.getTopView()
        } else null
    }

    private fun initRecyclerViewAndCallbacks(recyclerView: RecyclerView) {
        destroyCallbacks()
        this.recyclerView = recyclerView

        setupCallbacks()
        gravityScroller = initializeGravityScroller(this.recyclerView)
        //snapToTargetExistingView()
    }

    private fun initializeGravityScroller(recyclerView: RecyclerView): Scroller {
        return Scroller(recyclerView.context, DecelerateInterpolator())
    }

    @Throws(IllegalStateException::class)
    private fun setupCallbacks() {
        if (recyclerView.onFlingListener != null) {
            throw IllegalStateException("An instance of OnFlingListener already set.")
        }
        recyclerView.addOnScrollListener(scrollListener)
        recyclerView.onFlingListener = this
    }

    private fun destroyCallbacks() {
        recyclerView.removeOnScrollListener(scrollListener)
        recyclerView.onFlingListener = null
    }
}