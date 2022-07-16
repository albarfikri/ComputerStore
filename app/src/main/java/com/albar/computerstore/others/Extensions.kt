package com.albar.computerstore.others

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.enable() {
    isEnabled = true

}

fun View.disable() {
    isEnabled = false
}

fun Fragment.toastLong(msg: String?) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}

fun Fragment.toastShort(msg: String?) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun View.snackBarLong(msg: String) {
    Snackbar.make(this, msg, Toast.LENGTH_LONG).show()
}

fun View.snackBarShort(msg: String) {
    Snackbar.make(this, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.toastLong(msg: String?) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
}

fun Activity.toastShort(msg: String?) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}

fun View.startAnimation(animation: Animation, onEnd: () -> Unit) {
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) = Unit

        override fun onAnimationEnd(animation: Animation) {
            onEnd()
        }

        override fun onAnimationRepeat(p0: Animation?) = Unit

    })
    this.startAnimation(animation)
}
