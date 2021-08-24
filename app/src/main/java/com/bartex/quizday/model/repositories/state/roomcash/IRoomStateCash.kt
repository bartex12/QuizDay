package com.bartex.quizday.model.repositories.state.roomcash

import com.bartex.quizday.model.entity.State
import io.reactivex.rxjava3.core.Single

interface IRoomStateCash {
    fun doStatesCash(listStates: List<State>): Single<List<State>>
    fun getStatesFromCash():Single<List<State>>//получение списка пользователей из кэша
}