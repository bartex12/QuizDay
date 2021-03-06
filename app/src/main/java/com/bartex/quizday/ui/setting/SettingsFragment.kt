package com.bartex.quizday.ui.setting

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.bartex.quizday.R

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_setting, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()

        //BackgroundColor экрана настроек
        listView.setBackgroundColor(Color.argb(80, 160,244,186));
    }
}