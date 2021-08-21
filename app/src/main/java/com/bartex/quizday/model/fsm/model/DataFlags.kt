package com.bartex.quizday.model.fsm.model

import com.bartex.quizday.model.entity.State

data class DataFlags(
        var flagsInQuiz:Int = 2, //количество вопросов в викторите
        var guessRows:Int = 0, // Количество строк с кнопками вариантов
        var correctAnswer : String? = null,  //правильный ответ
        var correctAnswers:Int = 0,  //количество правильных ответов
        var totalGuesses:Int = 0 , // общее количество попыток
        var typeAnswer:Answer? = null, //тип ответа
        var listStates: MutableList<State> = mutableListOf(), //список всех стран
        val quizCountriesList: MutableList<State> = ArrayList(),  //страны текущей викторины
        var nextCountry:State? = null //
)
