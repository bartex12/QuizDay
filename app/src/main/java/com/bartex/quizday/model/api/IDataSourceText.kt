package com.bartex.quizday.model.api

import com.bartex.quizday.model.entity.TextEntity
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface IDataSourceText {
    @GET("api/random")
    fun getRandomGuess(): Single<List<TextEntity>>
}