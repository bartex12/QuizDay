package com.bartex.quizday.model.fsm.repo.settings

import android.content.Context
import android.media.AudioManager
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bartex.quizday.App
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.ui.adapters.ItemList
import java.security.SecureRandom

class SettingsProvider(val app: App) :ISettingsProvider{

    override fun updateSoundOnOff() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        val sound = sharedPreferences.getBoolean(Constants.SOUND, true)

        (app.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                .setStreamMute(AudioManager.STREAM_MUSIC, !sound)
    }

    override fun updateNumberFlagsInQuiz(dataFlags: DataFlags): DataFlags {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        val flagsNumber: String?  = sharedPreferences.getString(Constants.FLAGS_IN_QUIZ, 10.toString())
        flagsNumber?. let{
            dataFlags.flagsInQuiz =flagsNumber.toInt()
        }
        return dataFlags
    }

    override fun getGuessRows(dataFlags:DataFlags):DataFlags {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        // Получение количества отображаемых вариантов ответа
        val choices: String? = sharedPreferences.getString(Constants.CHOICES, 2.toString())
        choices?. let{
            dataFlags.guessRows = it.toInt() / 2
        }
        return dataFlags
    }
}