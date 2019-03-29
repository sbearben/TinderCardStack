package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.cardView.CardStackLayoutManager

class CardStackFlingListener(
    private var recyclerView: RecyclerView
) : RecyclerView.OnFlingListener() {

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        private var hasScrolled = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && hasScrolled) {
                hasScrolled = false
                val layoutManager = recyclerView.layoutManager ?: return
                snapFromFling(layoutManager, 0, 0)
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

        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity) &&
                snapFromFling(layoutManager, velocityX, velocityY)
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        if (this.recyclerView === recyclerView) {
            return  // nothing to do
        }
        initRecyclerViewAndCallbacks(recyclerView)
    }

    private fun snapFromFling(lm: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Boolean {
        findSnapView(lm)?.let { targetView ->
            val layoutManager = lm as CardStackLayoutManager
            val x = targetView.translationX.toInt()
            val y = targetView.translationY.toInt()

            if (x != 0 || y != 0) {
                val state = layoutManager.state

                val horizontal = Math.abs(x) / targetView.width.toFloat()
                val vertical = Math.abs(y) / targetView.height.toFloat()

                if (horizontal > SWIPE_THRESHOLD || vertical > SWIPE_THRESHOLD) {
                    if (Direction.HORIZONTAL.contains(state.getDirection())) {
                        val scroller =
                            CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualSwipe, layoutManager)
                        scroller.targetPosition = state.topPosition
                        layoutManager.startSmoothScroll(scroller)
                    } else {
                        val scroller =
                            CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualCancel, layoutManager)
                        scroller.targetPosition = state.topPosition
                        layoutManager.startSmoothScroll(scroller)
                    }
                } else {
                    val scroller =
                        CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualCancel, layoutManager)
                    scroller.targetPosition = state.topPosition
                    layoutManager.startSmoothScroll(scroller)
                }
                return true
            }
        }
        return false
    }

    private fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return if (layoutManager is CardStackLayoutManager) {
            layoutManager.getTopView()
        } else null
    }

    private fun initRecyclerViewAndCallbacks(recyclerView: RecyclerView) {
        destroyCallbacks()
        this.recyclerView = recyclerView
        setupCallbacks()
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