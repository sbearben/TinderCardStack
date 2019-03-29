package uk.co.victoriajanedavis.tindercardstack.cardView

import android.content.Context
import android.graphics.PointF
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.*

class CardStackLayoutManager(
    private val context: Context
) : RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider,
    ViewManipulator by ViewManipulatorImpl() {

    val state = State()

    private val visibleCount = 3


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, s: RecyclerView.State) {
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
            position in state.topPosition..maxAdapterPosition -> {
                //Log.d("findViewByPosition_1", "adapter position: $position, childAt: ${maxAdapterPosition-position}, childCount: $childCount, maxAdapterPosition: $maxAdapterPosition")
                getChildAt(maxAdapterPosition-position)
            }
            //else -> super.findViewByPosition(position)
            else -> null
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, s: RecyclerView.State): Int {
        if (state.status != Status.SwipeAnimating) {
            //Log.d("CardStackLayoutManager", "scrollHorizontal($dx)")
            state.dx -= dx
            update(recycler)
            return dx
        }
        return 0
    }

    override  fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, s: RecyclerView.State): Int {
        if (state.status != Status.SwipeAnimating) {
            //Log.d("CardStackLayoutManager", "scrollVertical($dy)")
            state.dy -= dy
            update(recycler)
            return dy
        }
        return 0
    }

    // TODO: this method can be vastly cleaned up once I understand what it's doing
    override fun onScrollStateChanged(scrollState: Int) {
        when(scrollState) {
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                //Log.d("CardStackLayoutManager", "topPosition: ${state.topPosition}, targetPosition: ${state.targetPosition}")
                //Log.d("CardStackLayoutManager", "SCROLL_STATE_DRAGGING")
                state.status = Status.Dragging
            }

            RecyclerView.SCROLL_STATE_SETTLING -> {

                /*
                //Log.d("CardStackLayoutManager", "SCROLL_STATE_SETTLING")
                Log.d("onScrollStateChanged", "SETTLING - 1")
                if (state.status != Status.PrepareSwipeAnimation) {
                    Log.d("onScrollStateChanged", "SETTLING - 2")
                    if (state.targetPosition == RecyclerView.NO_POSITION) {
                        Log.d("onScrollStateChanged", "SETTLING - 3, status: ${state.status}")  // CALLED ON MANUAL CANCEL (status usually RewindAnimating but I saw Dragging once
                        state.setFieldsToIdle()
                    } else {
                        Log.d("onScrollStateChanged", "SETTLING - 4")  // CALLED ON REWIND
                        if (state.targetPosition > state.topPosition) {
                            Log.d("onScrollStateChanged", "SETTLING - 5")
                            state.status = Status.PrepareSwipeAnimation
                        } else if (state.topPosition > state.targetPosition) {
                            Log.d("onScrollStateChanged", "SETTLING - 6")
                            //state.status = Status.RewindAnimating
                            state.status = Status.PrepareRewindAnimation
                        }
                    }
                }
                */

            }

            RecyclerView.SCROLL_STATE_IDLE -> {

                Log.d("onScrollStateChanged", "IDLE - 8")
                state.setFieldsToIdle()

                /*
                //Log.d("CardStackLayoutManager", "SCROLL_STATE_IDLE")
                Log.d("onScrollStateChanged", "IDLE - 1")
                if(state.status == Status.PrepareSwipeAnimation) {
                    Log.d("onScrollStateChanged", "IDLE - 2")
                    if (state.targetPosition == RecyclerView.NO_POSITION) {
                        Log.d("onScrollStateChanged", "IDLE - 3")
                        state.setFieldsToIdle()
                    } else {
                        Log.d("onScrollStateChanged", "IDLE - 4")
                        if (state.targetPosition > state.topPosition) {
                            Log.d("onScrollStateChanged", "IDLE - 5")
                            smoothScrollToNext(state.targetPosition)
                        } else if(state.topPosition > state.targetPosition) {
                            Log.d("onScrollStateChanged", "IDLE - 6")
                            smoothScrollToPrevious(state.targetPosition)
                        } else {
                            Log.d("onScrollStateChanged", "IDLE - 7")
                            state.setFieldsToIdle()
                        }
                    }
                } else {
                    Log.d("onScrollStateChanged", "IDLE - 8")  // CALLED EVERYTIME
                    state.setFieldsToIdle()
                }
                */

            }
        }
    }

    override fun scrollToPosition(position: Int) {
        Log.d("CardStackLayoutManager", "scrollToPosition($position)")
        if(scrollPositionIsInvalid(position)) {
            state.setFieldsToIdle()
        } else if (state.status == Status.Idle) {
            state.topPosition = position
            requestLayout()
        }
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, s: RecyclerView.State, position: Int) {
        Log.d("CardStackLayoutManager", "smoothScrollToPosition($position)")
        /*
        if(scrollPositionIsInvalid(position)) {
            state.setFieldsToIdle()
        } else if (state.status == Status.Idle) {
            smoothScrollToPosition(position)
        }
        */
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
        //smoothScrollToNext(CardStackSmoothScroller.ScrollType.AutomaticPositiveSwipe)
        smoothScrollToPosition(state.topPosition+1, CardStackSmoothScroller.ScrollType.AutomaticPositiveSwipe)
    }

    internal fun initiateNegativeSwipe() {
        //smoothScrollToNext(CardStackSmoothScroller.ScrollType.AutomaticNegativeSwipe)
        smoothScrollToPosition(state.topPosition+1, CardStackSmoothScroller.ScrollType.AutomaticNegativeSwipe)
    }

    internal fun initiateRewind() {
        //smoothScrollToPrevious()
        smoothScrollToPosition(state.topPosition-1, CardStackSmoothScroller.ScrollType.AutomaticRewind)
    }

    private fun smoothScrollToPosition(position: Int, scrollType: CardStackSmoothScroller.ScrollType) {
        if(scrollPositionIsInvalid(position)) {
            state.setFieldsToIdle()
        } else if (state.status == Status.Idle) {
            if (position > state.topPosition) {  // In the case of a swipe
                smoothScrollToNext(position, scrollType)
            } else {  // In the case of a rewind
                smoothScrollToPrevious(position)
            }
        }
    }

    // Swipe -> targetPosition > topPosition (card behind)
    private fun smoothScrollToNext(position: Int, scrollType: CardStackSmoothScroller.ScrollType) {
        //state.proportion = 0.0f
        state.targetPosition = position

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

        state.targetPosition = position
        //state.topPosition--

        //Log.d("CardStackLayoutManager", "smoothScroll(AutomaticRewind)")

        val scroller = CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.AutomaticRewind, this)
        //scroller.targetPosition = state.topPosition
        scroller.targetPosition = state.targetPosition
        startSmoothScroll(scroller)
    }

    private fun scrollPositionIsInvalid(position: Int) : Boolean {
        return position == state.topPosition || position < 0 || itemCount < position

    }

    private fun update(recycler: RecyclerView.Recycler) {
        state.width = width
        state.height = height

        //Log.d("CardStackLayoutManager", "topPosition: ${state.topPosition}, targetPosition: ${state.targetPosition}")

        detectSwipe(recycler)

        detectRewind(recycler)

        detachAndScrapAttachedViews(recycler)

        render(recycler)

        //recycleUnusedViews(recycler)

        /*
        if(status == Status.Dragging) {
            listener.onCardDragging(state.getDirection(), state.getRatio())
        }
        */
    }

    private fun detectSwipe(recycler: RecyclerView.Recycler) {

        // state.targetPosition == RecyclerView.NO_POSITION -> TRUE when manual swipe
        // state.targetPosition > state.topPosition -> TRUE when auto swipe
        if (state.status == Status.PrepareSwipeAnimation && (state.targetPosition == RecyclerView.NO_POSITION || state.targetPosition > state.topPosition)) {
            Log.d("CardStackLayoutManager", "update(first) \n" +
                    "state.targetPosition == RecyclerView.NO_POSITION: ${state.targetPosition == RecyclerView.NO_POSITION}\n" +
                    "state.topPosition < state.targetPosition: ${state.topPosition < state.targetPosition}")
            if (Math.abs(state.dx) > width || Math.abs(state.dy) > height) {
                getTopView()?.also { view ->
                    Log.d("CardStackAdapter", "removeAndRecycleView 1")
                    removeAndRecycleView(view, recycler)
                }

                state.status = Status.SwipeAnimating  // this causes scrollHorizontally/VerticallyBy to stop calling update() until animation is over
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

                Log.d("CardStackLayoutManager", "update(second)")
            }
        }
    }

    private fun detectRewind(recycler: RecyclerView.Recycler) {
        if (state.status == Status.PrepareRewindAnimation) {
            //recycleUnusedViews(recycler)
            val upperAdapterBounds = state.topPosition + visibleCount - 1
            Log.d("CardStackAdapter", "removeAndRecycleView 3: before, upperAdapterBounds=$upperAdapterBounds")
            findViewByPosition(upperAdapterBounds+1)?.let { view ->
                Log.d("CardStackAdapter", "removeAndRecycleView 3: adapterPosition=${upperAdapterBounds+1}")
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

        Log.d("CardStackLayoutManager", "render()")

        val endPoint = itemCount.coerceAtMost(state.topPosition+visibleCount) // this essentially is a min function
        (state.topPosition until endPoint).forEach { i ->
            val child = recycler.getViewForPosition(i)

            //Log.d("CardStackLayoutManager", "addChild($i)")

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
                updateTranslation(child, currentIndex)
                updateScale(child, state, currentIndex)
                resetRotation(child)
                resetOverlay(child)
            }
        }
    }

    private fun recycleUnusedViews(recycler: RecyclerView.Recycler) {
        val childCount = childCount
        if (childCount == 0) return

        (0 until childCount).forEach { i ->
            val view = getChildAt(i)
            if (view != null) {
                val adapterPosition = getPosition(view)
                val upperAdapterBounds = state.topPosition + visibleCount - 1
                if (adapterPosition < state.topPosition || adapterPosition > upperAdapterBounds) {
                    Log.d(
                        "CardStackAdapter",
                        "removeAndRecycleView 2: i=$i, adapterPosition=$adapterPosition, childCount=$childCount"
                    )
                    removeAndRecycleView(view, recycler)
                }
            }
        }
    }
}