package com.albar.computerstore.others

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.albar.computerstore.R

fun Context.rotateClose(): Animation = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim)
