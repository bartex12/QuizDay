package com.bartex.quizday.ui.flags.tabs.regions

import androidx.preference.PreferenceManager
import com.bartex.quizday.App
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.ui.flags.tabs.regions.IPreferenceHelper

class PreferenceHelper(val app: App): IPreferenceHelper {

    override fun savePositionState(position:Int) {

        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putInt(Constants.FIRST_POSITION_STATE, position)
                .apply()
    }

    override fun getPositionState(): Int {
        val position = PreferenceManager.getDefaultSharedPreferences(app)
                .getInt(Constants.FIRST_POSITION_STATE, 0)
        return position
    }

    override fun saveCurrentRegion(currentRegion: String) {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putString(Constants.CURRENT_REGION, currentRegion)
                .apply()
    }

    override fun getCurrentRegion():String{
      val currentRegion =  PreferenceManager.getDefaultSharedPreferences(app)
                .getString(Constants.CURRENT_REGION, Constants.REGION_ALL)!!
        return currentRegion
    }
}