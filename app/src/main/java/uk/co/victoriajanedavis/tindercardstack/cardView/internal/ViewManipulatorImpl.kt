package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import android.util.Log
import android.view.View
import uk.co.victoriajanedavis.tindercardstack.R
import uk.co.victoriajanedavis.tindercardstack.cardView.internal.Direction.*

class ViewManipulatorImpl : ViewManipulator {

    private val scaleInterval = 0.95f
    private val swipeThreshold = 0.3f
    private val maxDegree = 20.0f

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
        view.findViewById<View>(R.id.left_overlay)?.apply {
            alpha = 0.0f
        }
        view.findViewById<View>(R.id.right_overlay)?.apply {
            alpha = 0.0f
        }
        view.findViewById<View>(R.id.top_overlay)?.apply {
            alpha = 0.0f
        }
        view.findViewById<View>(R.id.bottom_overlay)?.apply {
            alpha = 0.0f
        }
    }

    override fun updateTranslation(view: View, state: State) {
        view.translationX = state.dx.toFloat()
        view.translationY = state.dy.toFloat()
    }

    override fun updateTranslation(view: View, index: Int) {
        // Stub for now
    }

    override fun updateScale(view: View, state: State, index: Int) {
        val nextIndex = index - 1
        val currentScale = 1.0f - index * (1.0f - scaleInterval)
        val nextScale = 1.0f - nextIndex * (1.0f - scaleInterval)
        val targetScale = currentScale + (nextScale - currentScale) * state.getRatio()

        view.scaleX = targetScale
        view.scaleY = targetScale
    }

    override fun updateRotation(view: View, state: State) {
        val degree = state.dx * maxDegree / state.width * state.proportion
        //Log.d("ViewManipulator", "state.dx: ${state.dx}, state.width: ${state.width}, degree: $degree")
        view.rotation = degree
    }

    override fun updateOverlay(view: View, state: State) {
        val leftOverlay = view.findViewById<View>(R.id.left_overlay)?.apply {
            alpha = 0.0f
        }
        val rightOverlay = view.findViewById<View>(R.id.right_overlay)?.apply {
            alpha = 0.0f
        }
        val topOverlay = view.findViewById<View>(R.id.top_overlay)?.apply {
            alpha = 0.0f
        }
        val bottomOverlay = view.findViewById<View>(R.id.bottom_overlay)?.apply {
            alpha = 0.0f
        }

        val direction = state.getDirection()
        when (direction) {
            Left -> leftOverlay?.alpha = state.getRatio()
            Right -> rightOverlay?.alpha = state.getRatio()
            Top -> topOverlay?.alpha = state.getRatio()
            Bottom -> bottomOverlay?.alpha = state.getRatio()
        }
    }
}