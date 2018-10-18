package fr.omi.sevenwondersduel

import android.os.Build
import android.view.View
import android.view.View.DragShadowBuilder

fun View.startDragAndDrop(data: Any) {
    if (Build.VERSION.SDK_INT >= 24) {
        startDragAndDrop(null, DragShadowBuilder(this), data, 0)
    } else {
        @Suppress("DEPRECATION")
        startDrag(null, DragShadowBuilder(this), data, 0)
    }
}