package com.bartex.quizday.model.fsm.repo

import com.bartex.quizday.model.entity.TextEntity
import com.bartex.quizday.model.fsm.entity.DataText

interface ITextQuiz {
    fun resetQuiz(
        listTextGuess: MutableList<TextEntity>,
        dataText: DataText,
        category: Integer
    ): DataText

    fun loadNextGuess(dataText: DataText): DataText
    fun getTypeAnswer(guess: String, dataText: DataText): DataText
}