package uk.co.victoriajanedavis.tindercardstack.cardView

import androidx.annotation.IdRes
import uk.co.victoriajanedavis.tindercardstack.R


class Settings(
    val swipeThreshold: Float = 0.3f,
    val visibleCount: Int = 3,
    val scaleInterval: Float = 0.95f,
    val maxDegree: Float = 20f,
    val translationInterval: Float = 8f,

    val stackFrom: StackFrom = StackFrom.None,
    val directions: List<Direction> = Direction.HORIZONTAL,

    val positiveSwipeAnimation: BaseAnimationSetting = PositiveSwipeAnimationSetting(),
    val negativeSwipeAnimation: BaseAnimationSetting = NegativeSwipeAnimationSetting(),
    val rewindAnimation: BaseAnimationSetting = RewindAnimationSetting(),

    val overlayIds: OverlayIdsHolder = OverlayIdsHolder()
)


class OverlayIdsHolder(
    @IdRes val leftOverlayId: Int = R.id.left_overlay,
    @IdRes val rightOverlayId: Int = R.id.right_overlay,
    @IdRes val topOverlayId: Int = R.id.top_overlay,
    @IdRes val bottomOverlayId: Int = R.id.bottom_overlay
)