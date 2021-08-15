package com.bartex.quizday.ui.flags

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.model.TestFlagClass

class FlagsViewModel(application: Application) :AndroidViewModel(application)  {

    val repo:IRepo = RepoImpl(application)

  fun   getFlags(): MutableList<TestFlagClass>{
      return repo.getFlags()
  }
}