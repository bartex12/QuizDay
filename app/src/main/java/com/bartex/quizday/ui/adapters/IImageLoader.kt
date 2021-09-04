package com.bartex.quizday.ui.adapters

import android.widget.ImageView

interface IImageLoader<T> {
    fun loadInto(url: String, container: T)
}