package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import uk.co.victoriajanedavis.tindercardstack.cardView.CardStackLayoutManager

class CardStackSnapHelper : SnapHelper() {

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray? {
        return IntArray(2)
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        return if (layoutManager is CardStackLayoutManager) {
            layoutManager.state.topPosition
        } else RecyclerView.NO_POSITION
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        return if (layoutManager is CardStackLayoutManager) {
            layoutManager.getTopView()
        } else null
    }
}