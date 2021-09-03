package com.bartex.quizday.ui.flags.mistakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import com.bartex.quizday.model.repositories.state.roomcash.RoomStateCash
import com.bartex.quizday.room.Database
import com.bartex.quizday.room.tables.RoomState
import com.bartex.quizday.ui.flags.regions.IPreferenceHelper
import com.bartex.quizday.ui.flags.regions.PreferenceHelper

class MistakesViewModel(
        private var helper : IPreferenceHelper = PreferenceHelper(App.instance),
        private var roomCash : IRoomStateCash =  RoomStateCash(Database.getInstance() as Database)
):ViewModel() {


    fun getPositionState(): Int{
        return helper.getPositionState()
    }

    fun savePositionState(position: Int){
        helper.savePositionState(position)
    }

  fun  getAllMistakesLive():LiveData<List<RoomState>>{
    return  roomCash.getAllMistakesLive()
  }

}