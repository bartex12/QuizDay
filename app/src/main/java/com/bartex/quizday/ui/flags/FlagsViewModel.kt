package com.bartex.quizday.ui.flags

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.api.IDataSourceState
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.Action
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.model.Answer
import com.bartex.quizday.model.fsm.model.DataFlags
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

    companion object{
        const val TAG = "33333"
        const val baseUrl =  "https://restcountries.eu/rest/v2/"
    }

    private val listStates = MutableLiveData<StatesSealed>()

    fun getStatesSealed() : LiveData<StatesSealed> {
        return listStates
    }

    fun loadDataSealed(){
        //начинаем загрузку данных
        listStates.value = StatesSealed.Loading(null)

        statesRepo.getStates()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({states->
                // если данные загружены - выставляем value в MutableLiveData
                listStates.value = StatesSealed.Success(state = states)
            },{error ->
                //если произошла ошибка - выставляем value в MutableLiveData в ошибку
                listStates.value = StatesSealed.Error(error = error)
            })
    }

    //=================================================

    private val quizState: MutableLiveData<IFlagState> = MutableLiveData<IFlagState>()
    private var storage: IFlagQuiz = FlagQuiz(App.instance)
    var dataflags:DataFlags = DataFlags()


    //начальное состояние не имеет предыдущего
    fun resetQuiz(listStates: MutableList<State>){
        dataflags.listStates = listStates //передаём список в класс данных
        dataflags =  storage.resetQuiz(dataflags) //подготовка переменных и списков
        quizState.value =  ReadyState(dataflags) //передаём полученные данные в состояние
    }

    fun answer(currentState: IFlagState, guess:String){

        dataflags = storage.getTypeAnswer(guess, dataflags)

        when(dataflags.typeAnswer){
          Answer.NotWell ->  quizState.value = currentState.consumAction(Action.OnNotWellClicked(dataflags))
          Answer.WellNotLast ->  quizState.value = currentState.consumAction(Action.OnWellNotLastClicked(dataflags))
          Answer.WellAndLast ->  quizState.value =  currentState.consumAction(Action.OnWellAndLastClicked(dataflags))
        }
    }

    fun loadNextFlag(dataFlags:DataFlags){
        dataflags =  storage.loadNextFlag(dataFlags)
        quizState.value =  ReadyState(dataflags)//todo
    }


    fun getCurrentState(): LiveData<IFlagState> {
        return quizState
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
}