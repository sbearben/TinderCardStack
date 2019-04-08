package uk.co.victoriajanedavis.tindercardstack.cardView.internal

import android.view.View

internal interface ViewManipulator {

    fun resetTranslation(view: View)

    fun resetScale(view: View)

    fun resetRotation(view: View)

    fun resetOverlay(view: View)

    fun updateTranslation(view: View, state: State)

    fun updateTranslation(view: View, state: State, index: Int)

    fun updateScale(view: View, state: State, index: Int)

    fun updateRotation(view: View, state: State)

    fun updateOverlay(view: View, state: State)
}