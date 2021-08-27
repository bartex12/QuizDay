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
import com.bartex.quizday.model.fsm.repo.settings.ISettingsProvider
import com.bartex.quizday.model.fsm.repo.settings.SettingsProvider
import com.bartex.quizday.model.fsm.substates.ReadyState
import com.bartex.quizday.model.repositories.state.IStatesRepo
import com.bartex.quizday.model.repositories.state.StatesRepo
import com.bartex.quizday.model.repositories.state.roomcash.RoomStateCash
import com.bartex.quizday.room.Database
import com.bartex.quizday.ui.adapters.ItemList
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
            .create(IDataSourceState::class.java),
        roomCash = RoomStateCash(Database.getInstance() as Database)),
        private val storage: IFlagQuiz = FlagQuiz(),
        private val settingProvider: ISettingsProvider = SettingsProvider(App.instance),
) : ViewModel()  {

    private val listStates = MutableLiveData<StatesSealed>()
    private val quizState: MutableLiveData<IFlagState> = MutableLiveData<IFlagState>()

    var dataFlags:DataFlags = DataFlags() // здесь храним данные состояний конечного автомата
    private var listOfStates:MutableList<State> = mutableListOf() //Здесь храним список стран из сети

    fun getStatesSealed(isNetworkAvailable:Boolean) : LiveData<StatesSealed> {
        loadDataSealed(isNetworkAvailable)
        return listStates
    }

    private fun loadDataSealed(isNetworkAvailable:Boolean){
        listStates.value = StatesSealed.Loading(0)

        statesRepo.getStates(isNetworkAvailable)
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

    //сохраняем список стран чтобы не пропадал при поворотах экрана
   fun saveListOfStates( listStates:MutableList<State>){
       listOfStates = listStates
   }

    //начальное состояние не имеет предыдущего
    fun resetQuiz(region:String){
        dataFlags =  storage.resetQuiz(listOfStates, dataFlags, region) //подготовка переменных и списков
        quizState.value =  ReadyState(dataFlags) //передаём полученные данные в состояние
    }
    //загрузить первый флаг
    fun loadFirstFlag(currentState: IFlagState, dataFlags:DataFlags){
        this.dataFlags =  storage.loadNextFlag(dataFlags)
        quizState.value =  currentState.executeAction(Action.OnNextFlagClicked(this.dataFlags))
    }

    //по типу ответа при щелчке по кнопке задаём состояние
    fun answer(currentState: IFlagState, guess:String){
        dataFlags = storage.getTypeAnswer(guess, dataFlags)
        when(dataFlags.typeAnswer){
            Answer.NotWell ->  quizState.value = currentState.executeAction(Action.OnNotWellClicked(dataFlags))
            Answer.WellNotLast ->  quizState.value = currentState.executeAction(Action.OnWellNotLastClicked(dataFlags))
            Answer.WellAndLast ->  quizState.value =  currentState.executeAction(Action.OnWellAndLastClicked(dataFlags))
        }
    }

    //загрузить следующий флаг
    fun loadNextFlag(currentState: IFlagState, dataFlags:DataFlags){
        this.dataFlags =  storage.loadNextFlag(dataFlags)
        quizState.value =  currentState.executeAction(Action.OnNextFlagClicked(this.dataFlags))
    }

    //обновить настройки звука
    fun updateSoundOnOff(){
        settingProvider.updateSoundOnOff()
    }

    //обновить количество вопросов в викторине
    fun updateNumberFlagsInQuiz(){
        dataFlags = settingProvider.updateNumberFlagsInQuiz(dataFlags)
    }

    //получить количество рядов кнопок с ответами
    fun getGuessRows():Int{
        dataFlags = settingProvider.getGuessRows(dataFlags)
        return dataFlags.guessRows
    }

    fun  getRegionList():List<ItemList> {
        return settingProvider.updateRegionList()
    }
}