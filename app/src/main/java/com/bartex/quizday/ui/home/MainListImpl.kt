package com.bartex.quizday.ui.home

import android.app.Application
import androidx.core.content.ContextCompat
import com.bartex.quizday.R
import com.bartex.quizday.model.MainList

class MainListImpl(val application:Application):IMainList {

    override fun getList(): List<MainList> {
        //получаем две строки
      return listOf(
              MainList(application.resources.getString(R.string.textQuizMain),
                      ContextCompat.getDrawable(application, R.drawable.text_x )!!),
              MainList(application.resources.getString(R.string.imageQuizMain),
                              ContextCompat.getDrawable(application, R.drawable.sm )!!)
      )
    }
}