package com.bartex.quizday.ui.flags.regions

import androidx.lifecycle.ViewModel
import com.bartex.quizday.App

class RegionViewModel(
        private var helper : IPreferenceHelper = PreferenceHelper(App.instance),
):ViewModel() {

    fun getPositionState(): Int{
        return helper.getPositionState()
    }

    fun savePositionState(position: Int){
        helper.savePositionState(position)
    }
}