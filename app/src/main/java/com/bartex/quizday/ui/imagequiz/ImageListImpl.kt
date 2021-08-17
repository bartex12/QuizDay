package com.bartex.quizday.ui.imagequiz

import android.app.Application
import androidx.core.content.ContextCompat
import com.bartex.quizday.R
import com.bartex.quizday.model.MainList

class ImageListImpl(val application: Application):IImageList {

    override fun getImageList(): List<MainList> {
        //получаем две строки
        return listOf(
                MainList(application.resources.getString(R.string.flags_of_states),
                        ContextCompat.getDrawable(application, R.drawable.flags )!!),
                MainList(application.resources.getString(R.string.anything),
                        ContextCompat.getDrawable(application, R.drawable.text_x )!!),
                MainList(application.resources.getString(R.string.anything),
                        ContextCompat.getDrawable(application, R.drawable.text_x )!!)
        )
    }
}
