package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import androidx.recyclerview.widget.RecyclerView
import uk.co.victoriajanedavis.tindercardstack.cardView.CardStackView
import kotlin.math.max

internal class CardStackDataObserver(
    private val recyclerView: CardStackView
) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        val manager = recyclerView.getCardStackLayoutManager()
        manager.state.topPosition = 0
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        // Do nothing
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        // Do nothing
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        // Do nothing
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        val manager = recyclerView.getCardStackLayoutManager()
        val topPosition = manager.state.topPosition

        if (manager.itemCount == 0) {
            manager.state.topPosition = 0
        } else if (topPosition > positionStart) {
            manager.state.topPosition = max(positionStart, topPosition - itemCount)
            //manager.state.topPosition = min(positionStart, manager.itemCount - 1)
        }
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        val manager = recyclerView.getCardStackLayoutManager()
        manager.removeAllViews()
    }
}