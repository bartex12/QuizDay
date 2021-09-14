package com.bartex.quizday.ui.flags.tabs.mistakes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import com.bartex.quizday.model.repositories.state.roomcash.RoomStateCash
import com.bartex.quizday.room.Database
import com.bartex.quizday.room.tables.RoomState
import com.bartex.quizday.model.preference.IPreferenceHelper
import com.bartex.quizday.model.preference.PreferenceHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

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

    fun removeMistakeFromDatabase(nameRus: String ){
            roomCash.removeMistakeFromDatabase(nameRus)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ isMistakeRemoved ->
                        if (isMistakeRemoved) {
                            Log.d(TAG, "MistakeRemoved: ")
                        }else{
                            Log.d(TAG, "NOT Removed ")
                        }
                    }, {error->
                        Log.d(TAG, "${error.message}")
                    })
    }

    companion object{
        const val TAG = "Quizday"
    }
}