package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import android.content.Context
import android.view.View
import uk.co.victoriajanedavis.tindercardstack.R
import uk.co.victoriajanedavis.tindercardstack.cardView.Direction
import uk.co.victoriajanedavis.tindercardstack.cardView.Settings
import uk.co.victoriajanedavis.tindercardstack.cardView.StackFrom

internal class ViewManipulatorImpl(
    private val context: Context,
    private val settings: Settings
) : ViewManipulator {


    override fun resetTranslation(view: View) {
        view.translationX = 0.0f
        view.translationY = 0.0f
    }

    override fun resetScale(view: View) {
        view.scaleX = 1.0f
        view.scaleY = 1.0f
    }

    override fun resetRotation(view: View) {
        view.rotation = 0.0f
    }

    override fun resetOverlay(view: View) {
        view.findViewById<View>(settings.overlayIds.leftOverlayId)?.apply {
            alpha = 0.0f
        }
        view.findViewById<View>(settings.overlayIds.rightOverlayId)?.apply {
            alpha = 0.0f
        }
        view.findViewById<View>(settings.overlayIds.topOverlayId)?.apply {
            alpha = 0.0f
        }
        view.findViewById<View>(settings.overlayIds.bottomOverlayId)?.apply {
            alpha = 0.0f
        }
    }

    override fun updateTranslation(view: View, state: State) {
        view.translationX = state.dx.toFloat()
        view.translationY = state.dy.toFloat()
    }

    override fun updateTranslation(view: View, state: State, index: Int) {
        val nextIndex = index - 1
        val translationPx = dpToPx(settings.translationInterval)
        val currentTranslation = (index * translationPx).toFloat()
        val nextTranslation = (nextIndex * translationPx).toFloat()
        val targetTranslation = currentTranslation - (currentTranslation - nextTranslation) * state.getRatio()

        when (settings.stackFrom) {
            StackFrom.None -> {}
            StackFrom.Top -> view.translationY = -targetTranslation
            StackFrom.Bottom -> view.translationY = targetTranslation
            StackFrom.Left -> view.translationX = -targetTranslation
            StackFrom.Right -> view.translationX = targetTranslation
        }
    }

    override fun updateScale(view: View, state: State, index: Int) {
        val nextIndex = index - 1
        val currentScale = 1.0f - index * (1.0f - settings.scaleInterval)
        val nextScale = 1.0f - nextIndex * (1.0f - settings.scaleInterval)
        val targetScale = currentScale + (nextScale - currentScale) * state.getRatio()

        when (settings.stackFrom) {
            StackFrom.None -> {
                view.scaleX = targetScale
                view.scaleY = targetScale
            }
            StackFrom.Top -> view.scaleX = targetScale
            StackFrom.Bottom -> view.scaleX = targetScale
            StackFrom.Left -> view.scaleY = targetScale
            StackFrom.Right -> view.scaleY = targetScale
        }
    }

    override fun updateRotation(view: View, state: State) {
        val degree = state.dx * settings.maxDegree / state.width * state.proportion
        //Log.d("ViewManipulator", "state.dx: ${state.dx}, state.width: ${state.width}, degree: $degree")
        view.rotation = degree
    }

    override fun updateOverlay(view: View, state: State) {
        val leftOverlay = view.findViewById<View>(settings.overlayIds.leftOverlayId)?.apply {
            alpha = 0.0f
        }
        val rightOverlay = view.findViewById<View>(settings.overlayIds.rightOverlayId)?.apply {
            alpha = 0.0f
        }
        val topOverlay = view.findViewById<View>(settings.overlayIds.topOverlayId)?.apply {
            alpha = 0.0f
        }
        val bottomOverlay = view.findViewById<View>(settings.overlayIds.bottomOverlayId)?.apply {
            alpha = 0.0f
        }

        val direction = state.getDirection()
        when (direction) {
            Direction.Left -> leftOverlay?.alpha = state.getRatio()
            Direction.Right -> rightOverlay?.alpha = state.getRatio()
            Direction.Top -> topOverlay?.alpha = state.getRatio()
            Direction.Bottom -> bottomOverlay?.alpha = state.getRatio()
        }
    }

    private fun dpToPx(dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}