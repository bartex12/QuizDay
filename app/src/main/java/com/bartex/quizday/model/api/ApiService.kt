package com.bartex.quizday.model.api

import com.bartex.quizday.model.entity.State
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface ApiService {

    @GET("all")
    fun getStates(): Single<List<State>>
}