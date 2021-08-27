package com.bartex.quizday.model.fsm.repo.settings

import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.ui.adapters.ItemList

interface ISettingsProvider {
    fun updateSoundOnOff()
    fun updateNumberFlagsInQuiz(dataFlags: DataFlags): DataFlags
    fun getGuessRows(dataFlags:DataFlags):DataFlags
    fun updateRegionList(): List<ItemList>
}