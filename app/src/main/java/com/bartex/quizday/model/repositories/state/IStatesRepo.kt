package com.bartex.quizday.model.repositories.state

import com.bartex.quizday.model.entity.State
import io.reactivex.rxjava3.core.Single

interface IStatesRepo {
    fun getStates(): Single<List<State>>
    fun getStatesFromCash(region:String): Single<List<State>>

}