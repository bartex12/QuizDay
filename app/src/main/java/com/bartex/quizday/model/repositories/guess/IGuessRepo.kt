package com.bartex.quizday.model.repositories.guess

import com.bartex.quizday.model.entity.TextEntity
import io.reactivex.rxjava3.core.Single

interface IGuessRepo {
    fun getGuess(): Single<List<TextEntity>>
}