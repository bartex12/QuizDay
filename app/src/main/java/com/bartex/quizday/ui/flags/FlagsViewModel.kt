package com.bartex.quizday.ui.flags

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.api.IDataSourceState
import com.bartex.quizday.model.common.Constants
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

    private val listQuizStates = MutableLiveData<StatesSealed>()
    private val currentQuizState: MutableLiveData<IFlagState> = MutableLiveData<IFlagState>()

    private var dataFlags:DataFlags = DataFlags() // здесь храним данные состояний конечного автомата
    private var listOfStates:MutableList<State> = mutableListOf() //Здесь храним список стран из сети
    private var region:String = Constants.REGION_EUROPE //Здесь храним текущий регион
    private var currentState:IFlagState = ReadyState(DataFlags()) //Здесь храним текущее состояние
    private var isNeedToCreateDialog:Boolean = true//Здесь храним флаг необходимости создания диалога


    fun isNeedToCreateDialog():Boolean{
        return isNeedToCreateDialog
    }

    fun setNeedToCreateDialog(isNeed:Boolean){
       isNeedToCreateDialog = isNeed
    }

    fun getStatesSealed(isNetworkAvailable:Boolean) : LiveData<StatesSealed> {
        loadDataSealed(isNetworkAvailable)
        return listQuizStates
    }

    private fun loadDataSealed(isNetworkAvailable:Boolean){
        listQuizStates.value = StatesSealed.Loading(0)

        statesRepo.getStates(isNetworkAvailable)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({states->
                listQuizStates.value = StatesSealed.Success(states = states)
            },{error ->
                listQuizStates.value = StatesSealed.Error(error = error)
            })
    }

    //получить состояние конечного автомата
    fun getCurrentState(): LiveData<IFlagState> {
        return currentQuizState
    }

    //сохраняем список стран чтобы не пропадал при поворотах экрана
   fun saveListOfStates( listStates:MutableList<State>){
       listOfStates = listStates
   }

    //начальное состояние не имеет предыдущего
    fun resetQuiz(){
        setNeedToCreateDialog(true) //возвращаем флаг разрешения создания диалога
        dataFlags =  storage.resetQuiz(listOfStates, dataFlags, region) //подготовка переменных и списков
        currentQuizState.value =  ReadyState(dataFlags) //передаём полученные данные в состояние
    }

    //загрузить следующий флаг
    fun loadNextFlag(dataFlags:DataFlags){
        this.dataFlags =  storage.loadNextFlag(dataFlags)
        currentQuizState.value =  currentState.executeAction(Action.OnNextFlagClicked(this.dataFlags))
    }

    //по типу ответа при щелчке по кнопке задаём состояние
    fun answer(guess:String){
        dataFlags = storage.getTypeAnswer(guess, dataFlags)
        when(dataFlags.typeAnswer){
            Answer.NotWell ->  currentQuizState.value = currentState.executeAction(Action.OnNotWellClicked(dataFlags))
            Answer.WellNotLast ->  currentQuizState.value = currentState.executeAction(Action.OnWellNotLastClicked(dataFlags))
            Answer.WellAndLast ->  currentQuizState.value =  currentState.executeAction(Action.OnWellAndLastClicked(dataFlags))
        }
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

    //сохраняем список регионов чтобы не пропадал при поворотах экрана
    fun saveRegion( newRegion:String){
        region = newRegion
    }
    fun getRegion( ):String{
      return  region
    }

    //сохраняем текущее состояние
    fun saveCurrentState( newState:IFlagState){
        currentState = newState
    }
    fun getState( ):IFlagState{
        return  currentState
    }
}