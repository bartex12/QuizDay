package com.bartex.quizday.ui.flags.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val newRegion = MutableLiveData<String>()

    fun update(currentRegion: String) {
        newRegion.value = currentRegion
    }
}