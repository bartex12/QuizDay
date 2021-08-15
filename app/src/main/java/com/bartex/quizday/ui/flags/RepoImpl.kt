package com.bartex.quizday.ui.flags

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.bartex.quizday.R
import com.bartex.quizday.model.TestFlagClass

class RepoImpl(val application: Application): IRepo {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getFlags(): MutableList<TestFlagClass> {
        val flags = mutableListOf<TestFlagClass>(
                TestFlagClass("Image1", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image2", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image3", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image4", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image5", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image6", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image7", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image8", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image9", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable),
                TestFlagClass("Image10", ContextCompat.getDrawable(application, R.drawable.sun_smail2) as Drawable)
        )
        return flags
    }
}