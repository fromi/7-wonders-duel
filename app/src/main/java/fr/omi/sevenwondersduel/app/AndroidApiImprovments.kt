package fr.omi.sevenwondersduel.app

import android.os.Build
import android.view.View
import android.view.View.DragShadowBuilder

fun View.startDragAndDrop() {
    if (Build.VERSION.SDK_INT >= 24) {
        startDragAndDrop(null, DragShadowBuilder(this), this, 0)
    } else {
        @Suppress("DEPRECATION")
        startDrag(null, DragShadowBuilder(this), this, 0)
    }
}

fun View.removeOnTouchListener() {
    setOnTouchListener(null)
}