package com.bartex.quizday.ui.flags.regions

interface IPreferenceHelper {
    fun savePositionState(position:Int)
    fun getPositionState(): Int
}