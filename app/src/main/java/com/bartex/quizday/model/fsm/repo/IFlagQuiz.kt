package com.bartex.quizday.model.fsm.repo

import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.entity.ButtonTag
import com.bartex.quizday.model.fsm.entity.DataFlags

interface IFlagQuiz {
    fun resetQuiz(listStates: MutableList<State>, dataFlags:DataFlags, region:String):DataFlags
    fun loadNextFlag(dataFlags:DataFlags):DataFlags
    fun getTypeAnswer(guess:String, dataFlags:DataFlags):DataFlags
    fun getTypeAnswerWithTag(tag: ButtonTag, dataFlags:DataFlags):DataFlags
}