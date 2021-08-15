package com.bartex.quizday.ui.imagequiz

import android.app.Application
import androidx.core.content.ContextCompat
import com.bartex.quizday.R
import com.bartex.quizday.model.MainList

class ImageListImpl(val application: Application):IImageList {

    override fun getImageList(): List<MainList> {
        //получаем две строки
        return listOf(
                MainList(application.resources.getString(R.string.flafs),
                        ContextCompat.getDrawable(application, R.drawable.flags )!!),
                MainList(application.resources.getString(R.string.capitals),
                        ContextCompat.getDrawable(application, R.drawable.ukazatel2 )!!),
                MainList(application.resources.getString(R.string.dogs),
                        ContextCompat.getDrawable(application, R.drawable.dogs )!!)
        )
    }
}
