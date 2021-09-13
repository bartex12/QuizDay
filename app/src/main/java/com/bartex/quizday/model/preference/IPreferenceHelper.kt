package com.bartex.quizday.model.preference

interface IPreferenceHelper {
    fun savePositionState(position:Int)
    fun getPositionState(): Int
}