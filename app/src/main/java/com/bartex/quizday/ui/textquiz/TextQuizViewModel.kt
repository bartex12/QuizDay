package com.bartex.quizday.ui.textquiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.R

class TextQuizViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = App.instance.resources.getString(R.string.no_now)
    }
    val text: LiveData<String> = _text
}