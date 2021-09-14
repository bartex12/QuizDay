package com.bartex.quizday.model.repositories.guess

import com.bartex.quizday.model.api.IDataSourceText
import com.bartex.quizday.model.entity.TextEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class GuessRepo(private val api: IDataSourceText): IGuessRepo {
    override fun getGuess(): Single<TextEntity> = api.getRandomGuess().subscribeOn(Schedulers.io())
}