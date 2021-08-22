package com.bartex.quizday.model.fsm.repo

import android.app.Application
import android.content.Context
import android.media.AudioManager
import androidx.preference.PreferenceManager
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.entity.Answer
import com.bartex.quizday.model.fsm.entity.DataFlags
import java.security.SecureRandom

class FlagQuiz(val app:Application):IFlagQuiz {

    //здесь сбрасываем переменные и очищаем списки а также формируем список с необходимым
    // числом флагов для новой викторины
    override fun resetQuiz(dataFlags:DataFlags):DataFlags {
        dataFlags.correctAnswers = 0  // Сброс количества правильных ответов
        dataFlags.totalGuesses = 0 //  Сброс общего количества попыток
        dataFlags.quizCountriesList.clear()  // Очистка предыдущего списка стран

        var flagCounter = 1
        //dataFlags.listStates - список с названиями стран, столицами, флагами
        val numberOfFlags = dataFlags.listStates.size
        // Добавление FLAGS_IN_QUIZ штук  случайных файлов в quizCountriesList
        while (flagCounter <= dataFlags.flagsInQuiz) {
            val randomIndex = SecureRandom().nextInt(numberOfFlags)
            // Получение случайного элемента списка - экземпляра класса State
            val state:State = dataFlags.listStates[randomIndex]
            // Если элемент списка еще не был выбран, добавляем его в список  для текущей викторины
            if (!dataFlags.quizCountriesList.contains(state)) {
                dataFlags.quizCountriesList.add(state)  // Добавить элемент в список для викторины
                ++flagCounter
            }
        }
        return dataFlags
    }

    //загрузка новых данных для викторины
    override fun loadNextFlag(dataFlags:DataFlags):DataFlags {
        // Получение  следующей страны
        dataFlags.nextCountry = dataFlags.quizCountriesList.removeAt(0)
        dataFlags.correctAnswer = dataFlags.nextCountry?.nameRus // Обновление правильного ответа
        dataFlags.listStates.shuffle()  // Перестановка стран в списке

        var correctIndex = 0
        for (i in 0 until dataFlags.listStates.size ){
            if(dataFlags.listStates[i].nameRus == dataFlags.correctAnswer){
                correctIndex = i //получение индекса правильного ответа
            }
        }
        // Помещение правильного ответа в конец listStates
        //dataFlags.listStates.add(dataFlags.listStates.removeAt(correctIndex))

        ++dataFlags.correctAnswers //увеличиваем номер ответа

        return dataFlags
    }

    override fun updateSoundOnOff() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        val sound = sharedPreferences.getBoolean(Constants.SOUND, true)

        (app.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                .setStreamMute(AudioManager.STREAM_MUSIC, !sound)
    }

    override fun updateNumberFlagsInQuiz(dataFlags:DataFlags):DataFlags {
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

    override fun getTypeAnswer(guess:String, dataflags:DataFlags) :DataFlags {
        ++dataflags.totalGuesses  // Увеличение количества попыток пользователя
        // Если ответ правилен
        if (guess == dataflags.correctAnswer) {
            //если ответ правильный и последний
            if (dataflags.correctAnswers == dataflags.flagsInQuiz) {
                dataflags.typeAnswer =Answer.WellAndLast
            }else {
                // Ответ правильный, но викторина не закончена
                dataflags.typeAnswer = Answer.WellNotLast
            }
        }else {
            // Неправильный ответ
            dataflags.typeAnswer =  Answer.NotWell
        }
        return dataflags
    }
}