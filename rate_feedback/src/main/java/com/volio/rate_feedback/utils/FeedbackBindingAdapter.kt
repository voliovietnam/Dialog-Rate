package com.volio.rate_feedback.utils


import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("drawable_src")
fun ImageView.setDrawable(drawable: Drawable?) {
    setImageDrawable(drawable)
}



