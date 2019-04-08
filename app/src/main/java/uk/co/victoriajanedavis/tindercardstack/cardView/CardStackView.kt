package uk.co.victoriajanedavis.tindercardstack.cardView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.CardStackDataObserver
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.CardStackFlingListener

class CardStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val observer = CardStackDataObserver(this)
    private val flingListener = CardStackFlingListener(this)

    override fun setLayoutManager(manager: LayoutManager?) {
        if (manager is CardStackLayoutManager) {
            super.setLayoutManager(manager)
        } else {
            throw IllegalArgumentException("CardStackView must set a layout manager of type CardStackLayoutManager.")
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (layoutManager == null) {
            layoutManager = CardStackLayoutManager(context)
        }

        getAdapter()?.apply {
            unregisterAdapterDataObserver(observer)
            onDetachedFromRecyclerView(this@CardStackView)
        }

        adapter?.registerAdapterDataObserver(observer)
        super.setAdapter(adapter)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if(layoutManager is CardStackLayoutManager) {
                Log.d("CardStackView", "MotionEvent.ACTION_DOWN")
                val manager = layoutManager as CardStackLayoutManager
                manager.updateProportion(event.x, event.y)
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    fun rightSwipe() {
        //swipe()
        getCardStackLayoutManager().initiatePositiveSwipe()
    }


    fun leftSwipe() {
        //swipe()
        getCardStackLayoutManager().initiateNegativeSwipe()
    }

    fun rewind() {
        getCardStackLayoutManager().initiateRewind()
    }

    internal fun getCardStackLayoutManager(): CardStackLayoutManager {
        val lm = layoutManager
        return if(lm is CardStackLayoutManager) lm
        else throw IllegalStateException("CardStackView must set a layout manager of type CardStackLayoutManager.")
    }
}
