package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder

fun View.enableDragAndDrop() {
    setOnTouchListener { v, event ->
        return@setOnTouchListener if (event.action == MotionEvent.ACTION_DOWN) {
            v.startDragAndDrop()
            true
        } else false
    }
}

fun View.startDragAndDrop() {
    if (Build.VERSION.SDK_INT >= 24) {
        startDragAndDrop(null, DragShadowBuilder(this), this, 0)
    } else {
        @Suppress("DEPRECATION")
        startDrag(null, DragShadowBuilder(this), this, 0)
    }
}

fun View.disableDragAndDrop() = setOnTouchListener(null)

fun View.removeDragListener() = setOnDragListener(null)

fun View.dpsToPx(dps: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps.toFloat(), resources.displayMetrics).toInt()
}

fun <T> MutableLiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, Observer {
        it?.let { nonNull -> observer(nonNull) }
    })
}

fun ConstraintLayout.transform(transformation: ConstraintSet.() -> Unit) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(this)
    transformation(constraintSet)
    constraintSet.applyTo(this)
}