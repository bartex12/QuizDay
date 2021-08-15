package com.bartex.quizday.ui.flags

import android.app.Application
import android.graphics.drawable.Drawable
import com.bartex.quizday.model.TestFlagClass

interface IRepo {
    fun   getFlags(): MutableList<TestFlagClass>
}