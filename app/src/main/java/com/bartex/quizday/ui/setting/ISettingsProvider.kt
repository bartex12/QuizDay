package com.bartex.quizday.ui.setting

import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.ui.adapters.ItemList

interface ISettingsProvider {
    fun updateSoundOnOff()
    fun updateNumberFlagsInQuiz(dataFlags: DataFlags): DataFlags
    fun getGuessRows(dataFlags:DataFlags):DataFlags
    fun updateImageStub(dataFlags:DataFlags):DataFlags
}