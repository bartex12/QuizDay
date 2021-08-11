package com.bartex.quizday.ui.textquiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TextQuizViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Вау!"
    }
    val text: LiveData<String> = _text
}