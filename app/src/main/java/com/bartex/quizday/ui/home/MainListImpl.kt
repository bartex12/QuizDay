package com.bartex.quizday.ui.home

import android.app.Application
import androidx.core.content.ContextCompat
import com.bartex.quizday.R
import com.bartex.quizday.ui.adapters.ItemList

class MainListImpl(private val application:Application):IMainList {

    override fun getList(): MutableList<ItemList> {
      return mutableListOf(
          ItemList(application.resources.getString(R.string.textQuizMain), R.drawable.text_t),
          ItemList(application.resources.getString(R.string.imageQuizMain),R.drawable.single_pazzl),
          ItemList(application.resources.getString(R.string.settings),null),
          ItemList(application.resources.getString(R.string.help), null)
      )
    }
}