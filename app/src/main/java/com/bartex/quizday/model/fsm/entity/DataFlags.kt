package com.bartex.quizday.model.fsm.entity

import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import java.io.Serializable


data class DataFlags(
        var flagsInQuiz:Int = 2, //количество вопросов в викторите
        var guessRows:Int = 2, // Количество строк с кнопками вариантов
        var correctAnswer : String? = null,  //правильный ответ
        var correctAnswers:Int = 0,  //количество правильных ответов
        var totalGuesses:Int = 0 , // общее количество попыток
        var typeAnswer:Answer? = null, //тип ответа
        var region:String = Constants.REGION_EUROPE, //текущий регион
        var nextCountry:State? = null, //следующая страна для угадывания флага
        var row:Int = 0,  //строка кнопок ответов
        var column:Int = 0, //столбец кнопок ответов
        var buttonNotWellAnswerList: MutableList<String> =  mutableListOf(), //список неправильных ответов
        var listStatesFromNet: MutableList<State> = mutableListOf(),//список стран из сети
        var listStates: MutableList<State> = mutableListOf(), //список стран с учётом выбранного региона
        val quizCountriesList: MutableList<State> = ArrayList()  //страны текущей викторины
):Serializable
