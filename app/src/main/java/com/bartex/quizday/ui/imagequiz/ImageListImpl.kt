package com.bartex.quizday.ui.imagequiz

import android.app.Application
import androidx.core.content.ContextCompat
import com.bartex.quizday.R
import com.bartex.quizday.ui.adapters.ItemList

class ImageListImpl(private val application: Application):IImageList {

    override fun getImageList(): List<ItemListPicture> {
        //получаем две строки
        return listOf(
            ItemListPicture(application.resources.getString(R.string.flags_of_states),
                        ContextCompat.getDrawable(application, R.drawable.flags )),
            ItemListPicture(application.resources.getString(R.string.anything),
                        ContextCompat.getDrawable(application, R.drawable.text_x )),
            ItemListPicture(application.resources.getString(R.string.anything),
                        ContextCompat.getDrawable(application, R.drawable.text_x ))
        )
    }
}
