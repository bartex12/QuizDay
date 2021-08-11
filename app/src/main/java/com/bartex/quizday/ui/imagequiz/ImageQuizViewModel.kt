package com.bartex.quizday.ui.imagequiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageQuizViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Ку!"
    }
    val text: LiveData<String> = _text
}