package com.bartex.quizday.ui.flags

import androidx.lifecycle.AndroidViewModel
import com.bartex.quizday.model.TestFlagClass
import android.app.Application as Application1

class FlagsViewModel(application: Application1) :AndroidViewModel(application)  {

    val repo:IRepo = RepoImpl(application)

  fun   getFlags(): MutableList<TestFlagClass>{
      return repo.getFlags()
  }
}