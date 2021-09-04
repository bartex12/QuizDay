package com.bartex.quizday.ui.adapters


interface IImageLoader<T> {
    fun loadInto(url: String, container: T)
}