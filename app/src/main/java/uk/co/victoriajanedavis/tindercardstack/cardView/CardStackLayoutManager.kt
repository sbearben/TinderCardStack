package uk.co.victoriajanedavis.tindercardstack.cardView

import android.content.Context
import android.graphics.PointF
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.*
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.CardStackSmoothScroller.ScrollType

class CardStackLayoutManager(
    context: Context,
    val settings: Settings = Settings()
) : RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider,
    ViewManipulator by ViewManipulatorImpl(context, settings) {

    internal val state = State()


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, s: RecyclerView.State) {
        //Log.d("CardStackLayoutManager", "onLayoutChildren()")
        update(recycler)
        if (s.didStructureChange()) {
            getTopView()?.let { view ->
                //listener.onCardAppeared(view, state.topPosition)
            }
        }
    }

    override fun findViewByPosition(position: Int): View? {
        val childCount = childCount
        val maxAdapterPosition = state.topPosition + childCount - 1

        return when {
            childCount == 0 -> null
            position in state.topPosition..maxAdapterPosition -> getChildAt(maxAdapterPosition-position)
            else -> null
            //else -> super.findViewByPosition(position)
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, s: RecyclerView.State): Int {
        //Log.d("CardStackLayoutManager", "scrollHorizontal($dx)")
        if (state.status != Status.SwipeAnimating) {
            state.dx -= dx
            update(recycler)
            return dx
        }
        return 0
    }

    override  fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, s: RecyclerView.State): Int {
        //Log.d("CardStackLayoutManager", "scrollVertical($dy)")
        if (state.status != Status.SwipeAnimating) {
            state.dy -= dy
            update(recycler)
            return dy
        }
        return 0
    }

    override fun onScrollStateChanged(scrollState: Int) {
        when(scrollState) {
            RecyclerView.SCROLL_STATE_DRAGGING -> state.status = Status.Dragging
            RecyclerView.SCROLL_STATE_SETTLING -> {}
            RecyclerView.SCROLL_STATE_IDLE -> state.setFieldsToIdle()
        }
    }

    override fun scrollToPosition(position: Int) {
        if(scrollPositionIsInvalid(position)) {
            state.setFieldsToIdle()
        } else if (state.status == Status.Idle) {
            state.topPosition = position
            requestLayout()
        }
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, s: RecyclerView.State, position: Int) {
        //Log.d("CardStackLayoutManager", "smoothScrollToPosition($position)")
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        return null
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    fun updateProportion(x: Float, y: Float) {
        if (state.topPosition < itemCount) {
            findViewByPosition(state.topPosition)?.let { view ->
                val half = height/2f
                state.proportion = -(y - half - view.top) / half
            }
        }
    }

    fun getTopView(): View? {
        return findViewByPosition(state.topPosition)
    }

    internal fun initiatePositiveSwipe() {
        executeIfPositionIsValid(state.topPosition+1) { position ->
            smoothScrollToNext(position, ScrollType.AutomaticPositiveSwipe)
        }
    }

    internal fun initiateNegativeSwipe() {
        executeIfPositionIsValid(state.topPosition+1) { position ->
            smoothScrollToNext(position, ScrollType.AutomaticNegativeSwipe)
        }
    }

    internal fun initiateRewind() {
        executeIfPositionIsValid(state.topPosition-1) { position -> smoothScrollToPrevious(position) }
    }

    private fun executeIfPositionIsValid(position: Int, lambda: (position: Int) -> Unit) {
        if(scrollPositionIsInvalid(position)) {
            state.setFieldsToIdle()
        } else if (state.status == Status.Idle) {
            lambda(position)
        }
    }

    private fun smoothScrollToPosition(position: Int, scrollType: ScrollType) {
        if(scrollPositionIsInvalid(position)) {
            state.setFieldsToIdle()
        } else if (state.status == Status.Idle) {
            if (position > state.topPosition) {  // swipe
                smoothScrollToNext(position, scrollType)
            } else {  // rewind
                smoothScrollToPrevious(position)
            }
        }
    }

    // Swipe -> targetPosition > topPosition (card behind)
    private fun smoothScrollToNext(position: Int, scrollType: ScrollType) {
        //state.proportion = 0.0f

        val scroller = CardStackSmoothScroller(scrollType, this)
        scroller.targetPosition = state.topPosition  // scroller.targetPosition is the "adapter position of the target item"
        startSmoothScroll(scroller)
    }

    // Rewind -> targetPosition == topPosition
    private fun smoothScrollToPrevious(position: Int) {
        getTopView()?.let { view ->
            //listener.onCardDisappeared(view, topPosition)
        }
        //state.proportion = 0.0f

        val scroller = CardStackSmoothScroller(ScrollType.AutomaticRewind, this)
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }

    private fun scrollPositionIsInvalid(position: Int) : Boolean {
        return position == state.topPosition || position < 0 || itemCount < position
    }

    private fun update(recycler: RecyclerView.Recycler) {
        state.width = width
        state.height = height

        detectPrepareSwipe(recycler)
        detectPrepareRewind(recycler)
        detachAndScrapAttachedViews(recycler)
        render(recycler)

        /*
        if(status == Status.Dragging) {
            listener.onCardDragging(state.getDirection(), state.getRatio())
        }
        */
    }

    private fun detectPrepareSwipe(recycler: RecyclerView.Recycler) {

        // state.targetPosition == RecyclerView.NO_POSITION -> TRUE when manual swipe
        // state.targetPosition > state.topPosition -> TRUE when auto swipe
        if (state.status == Status.PrepareSwipeAnimation) {
            if (Math.abs(state.dx) > width || Math.abs(state.dy) > height) {
                getTopView()?.also { view ->
                    Log.d("CardStackAdapter", "removeAndRecycleView 1")
                    removeAndRecycleView(view, recycler)
                }

                state.status = Status.SwipeAnimating  // causes scrollHorizontally/VerticallyBy to stop calling update() until animation is over
                state.topPosition++

                /*
                val direction = state.getDirection()
                Handler().post {
                    listener.onCardSwiped(direction)
                    val topView = getTopView()
                    if (topView != null) {
                        listener.onCardAppeared(getTopView(), state.topPosition)
                    }
                }
                */

                state.dx = 0
                state.dy = 0
            }
        }
    }

    private fun detectPrepareRewind(recycler: RecyclerView.Recycler) {
        if (state.status == Status.PrepareRewindAnimation) {
            val upperAdapterBounds = state.topPosition + settings.visibleCount - 1
            findViewByPosition(upperAdapterBounds)?.let { view ->
                Log.d("CardStackAdapter", "removeAndRecycleView 2: adapterPosition=${upperAdapterBounds+1}")
                removeAndRecycleView(view, recycler)
            }

            state.status = Status.RewindAnimating
            state.topPosition--
        }
    }

    private fun render(recycler: RecyclerView.Recycler) {
        val parentTop = paddingTop
        val parentLeft = paddingLeft
        val parentRight = width - paddingRight
        val parentBottom = height - paddingBottom

        val endPoint = itemCount.coerceAtMost(state.topPosition+settings.visibleCount) // this essentially is a min function
        (state.topPosition until endPoint).forEach { i ->
            val child = recycler.getViewForPosition(i)

            addView(child, 0)
            measureChildWithMargins(child, 0, 0)
            layoutDecoratedWithMargins(child, parentLeft, parentTop, parentRight, parentBottom)

            resetTranslation(child)
            resetScale(child)
            resetRotation(child)
            resetOverlay(child)

            if (i == state.topPosition) {
                updateTranslation(child, state)
                resetScale(child)
                updateRotation(child, state)
                updateOverlay(child, state)
            } else {
                val currentIndex = i - state.topPosition
                updateTranslation(child, state, currentIndex)
                updateScale(child, state, currentIndex)
                resetRotation(child)
                resetOverlay(child)
            }
        }
    }
}