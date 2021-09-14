package com.bartex.quizday.model.fsm.repo

import com.bartex.quizday.model.entity.TextEntity
import com.bartex.quizday.model.fsm.entity.DataText

class TextQuiz:ITextQuiz {
    override fun resetQuiz(
        listTextGuess: MutableList<TextEntity>,
        dataText: DataText,
        category: Integer
    ): DataText {
        TODO("Not yet implemented")
    }

    override fun loadNextGuess(dataText: DataText): DataText {
        TODO("Not yet implemented")
    }

    override fun getTypeAnswer(guess: String, dataText: DataText): DataText {
        TODO("Not yet implemented")
    }
}