package com.bartex.quizday.model.api

import com.bartex.quizday.model.entity.State
import io.reactivex.rxjava3.core.Single

interface IDataSource {
    fun getStates(): Single<List<State>>
}