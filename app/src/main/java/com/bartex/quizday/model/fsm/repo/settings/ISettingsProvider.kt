package com.bartex.quizday.model.fsm.repo.settings

import com.bartex.quizday.model.fsm.entity.DataFlags

interface ISettingsProvider {
    fun updateSoundOnOff()
    fun updateNumberFlagsInQuiz(dataFlags: DataFlags): DataFlags
    fun getGuessRows(dataFlags:DataFlags):DataFlags
}