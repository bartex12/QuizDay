package com.bartex.quizday.model.repositories

import com.bartex.quizday.model.entity.State
import io.reactivex.rxjava3.core.Single

interface IStatesRepo {
    fun getStates(): Single<List<State>>
}