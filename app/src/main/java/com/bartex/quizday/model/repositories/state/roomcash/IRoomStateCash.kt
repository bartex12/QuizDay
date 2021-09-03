package com.bartex.quizday.model.repositories.state.roomcash

import com.bartex.quizday.model.entity.State
import com.bartex.quizday.room.tables.RoomState
import io.reactivex.rxjava3.core.Single

interface IRoomStateCash {
    fun doStatesCash(listStates: List<State>): Single<List<State>>
    fun getStatesFromCash():Single<List<State>>//получение списка стран из кэша
    fun getRegionStatesFromCash(region:String):Single<List<State>> //получение списка стран с учётом региона

   fun writeMistakeInDatabase(mistakeAnswer:String): Single<Boolean> //делаeм запись в базе данных о том, что ответ неверный
    fun getMistakesFromDatabase(): Single<List<State>>
    fun isDatabaseFull(): Single<MutableList<State>>
    fun getStatesFromDatabase(): Single<MutableList<State>>
}