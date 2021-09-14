package com.bartex.quizday.ui.flags.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val newRegion = MutableLiveData<String>()
    val toolbarTitleFromFlag = MutableLiveData<String>()
    val toolbarTitleFromState = MutableLiveData<String>()

    fun updateRegion(currentRegion: String) {
        newRegion.value = currentRegion
    }

    fun updateToolbarTitleFromFlag(title:String){
        toolbarTitleFromFlag.value = title
    }

    fun updateToolbarTitleFromState(title:String){
        toolbarTitleFromState.value = title
    }
}