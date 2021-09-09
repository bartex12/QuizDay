package com.bartex.quizday.ui.flags.tabs.regions

interface IPreferenceHelper {
    fun savePositionState(position:Int)
    fun getPositionState(): Int
    fun saveCurrentRegion(currentRegion:String)
    fun getCurrentRegion():String
}