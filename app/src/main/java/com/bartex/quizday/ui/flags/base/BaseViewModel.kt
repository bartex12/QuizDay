package com.bartex.quizday.ui.flags.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.api.IDataSourceState
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.Action
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.model.fsm.repo.FlagQuiz
import com.bartex.quizday.model.fsm.repo.IFlagQuiz
import com.bartex.quizday.model.fsm.repo.settings.ISettingsProvider
import com.bartex.quizday.model.fsm.repo.settings.SettingsProvider
import com.bartex.quizday.model.fsm.substates.ReadyState
import com.bartex.quizday.model.repositories.state.IStatesRepo
import com.bartex.quizday.model.repositories.state.StatesRepo
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import com.bartex.quizday.model.repositories.state.roomcash.RoomStateCash
import com.bartex.quizday.room.Database
import com.bartex.quizday.ui.flags.StatesSealed
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

//todo убрать дублирование
open class BaseViewModel(
         var statesRepo: IStatesRepo = StatesRepo(
                api= Retrofit.Builder()
                        .baseUrl(Constants.baseUrl)
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .addConverterFactory(
                                GsonConverterFactory.create(
                                        GsonBuilder()
                                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                                .excludeFieldsWithoutExposeAnnotation()
                                                .create()
                                )).build().create(IDataSourceState::class.java),
                roomCash = RoomStateCash(Database.getInstance() as Database)),
         val storage: IFlagQuiz = FlagQuiz(),
         val settingProvider: ISettingsProvider = SettingsProvider(app=App.instance),
         val roomCash: IRoomStateCash = RoomStateCash(db =Database.getInstance() as Database)
):ViewModel() {

    //список стран из сети
     val listStatesFromNet = MutableLiveData<StatesSealed>()
    //список стран из базы
     val listStatesFromDatabase = MutableLiveData<MutableList<State>>()
    //состояние конечного автомата
     val currentQuizState: MutableLiveData<IFlagState> = MutableLiveData<IFlagState>()
    //заголовок тулбара во FlagFragment
     var toolbarTitleInFlags:MutableLiveData<String> = MutableLiveData<String>()

     var dataFlags: DataFlags = DataFlags() // здесь храним данные для состояний конечного автомата
     var listOfStates:MutableList<State> = mutableListOf() //Здесь храним список стран из сети
     var region:String = Constants.REGION_EUROPE //Здесь храним текущий регион
     var currentState: IFlagState = ReadyState(DataFlags()) //Здесь храним текущее состояние
     var isNeedToCreateDialog:Boolean = true//Здесь храним флаг необходимости создания диалога

    fun getNeedDialog():Boolean{
        return isNeedToCreateDialog
    }

    fun setNeedDialog(isNeed:Boolean){
        isNeedToCreateDialog = isNeed
    }

    fun getStatesSealed() : LiveData<StatesSealed> {
        loadDataSealed()
        return listStatesFromNet
    }

    private fun loadDataSealed(){
        listStatesFromNet.value = StatesSealed.Loading(0)

        statesRepo.getStates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({states->
                    listStatesFromNet.value = StatesSealed.Success(states = states)
                },{error ->
                    listStatesFromNet.value = StatesSealed.Error(error = error)
                })
    }

    //получить состояние конечного автомата
    fun getCurrentState(): LiveData<IFlagState> {
        return currentQuizState
    }

    //сохраняем список стран чтобы не пропадал при поворотах экрана
    fun saveListOfStates( listStates:MutableList<State>){
        dataFlags.listStatesFromNet = listStates //для удобства храним в данных
        listOfStates = listStates //а также храним во ViewModel
    }

    fun getDataFromDatabase(): LiveData<MutableList<State>> {
        loadDataFromDatabase()
        return listStatesFromDatabase
    }

    private fun loadDataFromDatabase() {
        roomCash.loadAllData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    listStatesFromDatabase.value = it
                },{
                    Log.d(TAG, "${it.message}")
                })
    }


    //начальное состояние не имеет предыдущего
    fun resetQuiz(){
        setNeedDialog(true) //возвращаем флаг разрешения создания диалога
        dataFlags =  storage.resetQuiz(listOfStates, dataFlags, region) //подготовка переменных и списков
        currentQuizState.value =  ReadyState(dataFlags) //передаём полученные данные в состояние
    }

    //загрузить следующий флаг
    fun loadNextFlag(dataFlags:DataFlags){
        this.dataFlags =  storage.loadNextFlag(dataFlags)
        currentQuizState.value =  currentState.executeAction(Action.OnNextFlagClicked(this.dataFlags))
    }



    //по типу ответа при щелчке по кнопке задаём состояние
//    fun answer(guess:String){
//        dataFlags = storage.getTypeAnswer(guess, dataFlags)
//        when(dataFlags.typeAnswer){
//            Answer.NotWell -> {
//                currentQuizState.value = currentState.executeAction(Action.OnNotWellClicked(dataFlags))
//            }
//            Answer.WellNotLast -> {
//                currentQuizState.value =  currentState.executeAction(Action.OnWellNotLastClicked(dataFlags))
//            }
//            Answer.WellAndLast -> {
//                currentQuizState.value = currentState.executeAction(Action.OnWellAndLastClicked(dataFlags))
//            }
//        }
//    }

    fun writeMistakeInDatabase() {
        dataFlags.correctAnswer?. let{
            roomCash.writeMistakeInDatabase(it)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ isMistakeWriten ->
                        if (isMistakeWriten) {
                            Log.d(TAG, "writeMistakeInDatabase: ")
                        }else{
                            Log.d(TAG, "NOT write ")
                        }
                    }, {
                        Log.d(TAG, "${it.message}")
                    })
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

    //сохраняем текущее состояние
    fun saveCurrentState( newState:IFlagState){
        currentState = newState
    }

    //сохраняем регион в классе данных
    fun saveRegion( newRegion:String){
        dataFlags.region = newRegion // в dataFlags
        region = newRegion  // в переменную ViewModel
    }

    fun getCurrentRegion( ):String{
        return  region
    }

    fun getRegionNameAndNumber( data: DataFlags):String{
        var regionSize = 0
        regionSize = when (region) {
            Constants.REGION_ALL -> {
                data.listStatesFromNet.size
            }
            else -> {
                data.listStatesFromNet.filter {
                    it.regionRus == data.region
                }.size
            }
        }
        return  "$region $regionSize"
    }


    companion object{
        const val TAG = "33333"
    }

}