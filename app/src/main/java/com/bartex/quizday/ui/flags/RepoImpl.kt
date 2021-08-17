package com.bartex.quizday.ui.flags

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.bartex.quizday.R
import com.bartex.quizday.model.TestFlagClass

class RepoImpl(val application: Application): IRepo {

    override fun getFlags(): MutableList<TestFlagClass> {
        val flags = mutableListOf<TestFlagClass>()
        for (i in 0 until 30){
          val name = application.resources.getString(R.string.state_fake)
          val test =   TestFlagClass("$name $i", (ContextCompat.getDrawable(application, R.drawable.fun_rojer)  as Drawable ))
            flags.add(test)
        }
        return flags
    }
}