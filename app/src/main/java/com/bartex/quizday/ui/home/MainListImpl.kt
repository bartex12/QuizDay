package com.bartex.quizday.ui.home

import android.app.Application
import androidx.core.content.ContextCompat
import com.bartex.quizday.R
import com.bartex.quizday.ui.adapters.ItemList

class MainListImpl(private val application:Application):IMainList {

    override fun getList(): List<ItemList> {
        //получаем две строки
      return listOf(
              ItemList(application.resources.getString(R.string.textQuizMain),
                      ContextCompat.getDrawable(application, R.drawable.text_x )!!),
              ItemList(application.resources.getString(R.string.imageQuizMain),
                              ContextCompat.getDrawable(application, R.drawable.sm )!!)
      )
    }
}