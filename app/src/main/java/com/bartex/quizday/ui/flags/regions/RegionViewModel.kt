package com.bartex.quizday.ui.flags.regions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import com.bartex.quizday.model.repositories.state.roomcash.RoomStateCash
import com.bartex.quizday.room.Database
import com.bartex.quizday.ui.flags.StatesSealed
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class RegionViewModel(
        private var helper : IPreferenceHelper = PreferenceHelper(App.instance),
        private var roomCash : IRoomStateCash =  RoomStateCash(Database.getInstance() as Database)
):ViewModel() {

    //список стран заданного региона из базы данных
    private val listRegionStatesFromRoom:MutableLiveData<StatesSealed> = MutableLiveData<StatesSealed>()

    fun getPositionState(): Int{
        return helper.getPositionState()
    }

    fun savePositionState(position: Int){
        helper.savePositionState(position)
    }

    fun getStatesFromCash(region: String){
        listRegionStatesFromRoom.value = StatesSealed.Loading(0)

      roomCash.getRegionStatesFromCash(region)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe({
                  listRegionStatesFromRoom.value = StatesSealed.Success(states = it)
              },{
                  listRegionStatesFromRoom.value = StatesSealed.Error(error = it)
              })
    }

    fun getListStatesFromRoom():LiveData<StatesSealed>{
        return listRegionStatesFromRoom
    }

}