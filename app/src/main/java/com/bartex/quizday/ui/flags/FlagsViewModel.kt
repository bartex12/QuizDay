package com.bartex.quizday.ui.flags

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.api.IDataSourceState
import com.bartex.quizday.model.common.Constants.baseUrl
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.Action
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.entity.Answer
import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.model.fsm.repo.FlagQuiz
import com.bartex.quizday.model.fsm.repo.IFlagQuiz
import com.bartex.quizday.model.fsm.substates.ReadyState
import com.bartex.quizday.model.repositories.IStatesRepo
import com.bartex.quizday.model.repositories.StatesRepo
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class FlagsViewModel(
    var statesRepo: IStatesRepo = StatesRepo(
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation()
                .create()
            ))
            .build()
            .create(IDataSourceState::class.java)
    )
) : ViewModel()  {

    private val listStates = MutableLiveData<StatesSealed>()

    private val quizState: MutableLiveData<IFlagState> = MutableLiveData<IFlagState>()
    private var storage: IFlagQuiz = FlagQuiz(App.instance)
    var dataflags:DataFlags = DataFlags()

    fun getStatesSealed() : LiveData<StatesSealed> {
        loadDataSealed()
        return listStates
    }

    private fun loadDataSealed(){
        listStates.value = StatesSealed.Loading(0)

        statesRepo.getStates()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({states->
                listStates.value = StatesSealed.Success(states = states)
            },{error ->
                listStates.value = StatesSealed.Error(error = error)
            })
    }

    //получить состояние конечного автомата
    fun getCurrentState(): LiveData<IFlagState> {
        return quizState
    }

    //начальное состояние не имеет предыдущего
    fun resetQuiz(listStates: MutableList<State>){
        dataflags.listStates = listStates //передаём список в класс данных
        dataflags =  storage.resetQuiz(dataflags) //подготовка переменных и списков
        quizState.value =  ReadyState(dataflags) //передаём полученные данные в состояние
    }
    //загрузить следующий флаг
    fun loadFirstFlag(currentState: IFlagState, dataFlags:DataFlags){
        dataflags =  storage.loadNextFlag(dataFlags)
        quizState.value =  currentState.executeAction(Action.OnNextFlagClicked(dataflags))
    }

    //по типу ответа при щелчке по кнопке задаём состояние
    fun answer(currentState: IFlagState, guess:String){
        dataflags = storage.getTypeAnswer(guess, dataflags)
        when(dataflags.typeAnswer){
            Answer.NotWell ->  quizState.value = currentState.executeAction(Action.OnNotWellClicked(dataflags))
            Answer.WellNotLast ->  quizState.value = currentState.executeAction(Action.OnWellNotLastClicked(dataflags))
            Answer.WellAndLast ->  quizState.value =  currentState.executeAction(Action.OnWellAndLastClicked(dataflags))
        }
    }

    //загрузить следующий флаг
    fun loadNextFlag(currentState: IFlagState, dataFlags:DataFlags){
        dataflags =  storage.loadNextFlag(dataFlags)
        quizState.value =  currentState.executeAction(Action.OnNextFlagClicked(dataflags))
    }

    fun updateSoundOnOff(){
        storage.updateSoundOnOff()
    }

    fun updateNumberFlagsInQuiz(){
        dataflags = storage.updateNumberFlagsInQuiz(dataflags)
    }

    fun getGuessRows():Int{
        dataflags = storage.getGuessRows(dataflags)
        return dataflags.guessRows
    }

    override fun onCleared() {
        super.onCleared()
        dataflags
    }
}