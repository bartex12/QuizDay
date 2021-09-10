package com.bartex.quizday.ui.flags.tabs.regions

import androidx.preference.PreferenceManager
import com.bartex.quizday.App
import com.bartex.quizday.model.common.Constants

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

}