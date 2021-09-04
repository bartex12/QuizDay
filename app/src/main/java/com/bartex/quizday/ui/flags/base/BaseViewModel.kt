package com.bartex.quizday.ui.flags.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import com.bartex.quizday.model.repositories.state.roomcash.RoomStateCash
import com.bartex.quizday.room.Database
import com.bartex.quizday.ui.flags.StatesSealed
import com.bartex.quizday.ui.flags.regions.IPreferenceHelper
import com.bartex.quizday.ui.flags.regions.PreferenceHelper

open class BaseViewModel(
        var helper : IPreferenceHelper = PreferenceHelper(App.instance),
        var roomCash : IRoomStateCash =  RoomStateCash(Database.getInstance() as Database)
):ViewModel() {

    val listOfData = MutableLiveData<StatesSealed>()

    open fun getPositionState(): Int{
        return helper.getPositionState()
    }

    open fun savePositionState(position: Int){
        helper.savePositionState(position)
    }

}