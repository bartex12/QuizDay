package com.bartex.quizday.ui.flags.mistakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import com.bartex.quizday.model.repositories.state.roomcash.RoomStateCash
import com.bartex.quizday.room.Database
import com.bartex.quizday.ui.flags.StatesSealed
import com.bartex.quizday.ui.flags.regions.IPreferenceHelper
import com.bartex.quizday.ui.flags.regions.PreferenceHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class MistakesViewModel(
        private var helper : IPreferenceHelper = PreferenceHelper(App.instance),
        private var roomCash : IRoomStateCash =  RoomStateCash(Database.getInstance() as Database)
):ViewModel() {

    private val listOfMistakes = MutableLiveData<StatesSealed>()

    fun getPositionState(): Int{
        return helper.getPositionState()
    }

    fun savePositionState(position: Int){
        helper.savePositionState(position)
    }

    fun getMistakes():LiveData<StatesSealed>{
        getMistakesFromDatabase()
        return listOfMistakes
    }

    private fun getMistakesFromDatabase(){
        listOfMistakes.value = StatesSealed.Loading(0)

        roomCash.getMistakesFromDatabase()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({states->
                    listOfMistakes.value = StatesSealed.Success(states = states)
                },{error ->
                    listOfMistakes.value = StatesSealed.Error(error = error)
                })
    }

}