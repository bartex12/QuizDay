package com.bartex.quizday.ui.flags.tabs.regions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.preference.IPreferenceHelper
import com.bartex.quizday.model.preference.PreferenceHelper
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import com.bartex.quizday.model.repositories.state.roomcash.RoomStateCash
import com.bartex.quizday.room.Database
import com.bartex.quizday.room.tables.RoomState

class RegionViewModel(
        private var helper : IPreferenceHelper = PreferenceHelper(App.instance),
        private var roomCash : IRoomStateCash =  RoomStateCash(Database.getInstance() as Database)
):ViewModel() {

    fun getPositionState(): Int{
        return helper.getPositionState()
    }

    fun savePositionState(position: Int){
        helper.savePositionState(position)
    }

    fun getAllDataLive():LiveData<List<RoomState>> {
       return roomCash.getAllDataLive()
    }
}