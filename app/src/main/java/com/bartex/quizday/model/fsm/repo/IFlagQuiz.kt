package com.bartex.quizday.model.fsm.repo

import com.bartex.quizday.model.fsm.model.Answer
import com.bartex.quizday.model.fsm.model.DataFlags

interface IFlagQuiz {
    fun resetQuiz(dataFlags:DataFlags):DataFlags
    fun updateSoundOnOff()
    fun updateNumberFlagsInQuiz(dataFlags:DataFlags):DataFlags
    fun getGuessRows(dataFlags:DataFlags):DataFlags
    fun getTypeAnswer(guess:String, dataflags:DataFlags):DataFlags
    fun loadNextFlag(dataFlags:DataFlags):DataFlags
}