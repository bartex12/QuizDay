package com.bartex.quizday.model.fsm.repo

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bartex.quizday.MainActivity
import com.bartex.quizday.R
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.model.Answer
import com.bartex.quizday.model.fsm.model.DataFlags
import com.bartex.quizday.model.fsm.substates.ReadyState
import com.bartex.quizday.ui.flags.FlagsViewModel
import com.bartex.quizday.ui.flags.ResultDialog
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import java.security.SecureRandom
import java.util.prefs.PreferenceChangeEvent

class FlagQuiz(val app:Application):IFlagQuiz {

    //private var flagsInQuiz = 2
   // private var quizCountriesList : MutableList<State> = ArrayList()
    // Регионы текущей викторины
    private var regionsSet  : Set<String>? = null

    // Для задержки загрузки следующего флага
    private lateinit var handler : Handler
    // Макет с викториной
    private var quizLinearLayout  : LinearLayout? = null
    // Номер текущего вопроса
   // private lateinit var questionNumberTextView  : TextView
    // Для правильного ответа
    //private lateinit var answerTextView : TextView
    //ImageView  Для вывода флага
    private var flagImageView  : ImageView? = null
    //прогресс бар на время загрузки
    private var progressBarFlags: ProgressBar? = null
    // Строки с кнопками
    private var guessLinearLayouts : Array<LinearLayout?> = arrayOfNulls(3)
    // Правильная страна для текущего флага
    //private var correctAnswer : String? = null
    // Количество правильных ответов -
    //если пользователь завершит викторину, это значение будет равно FLAGS_IN_QUIZ
    //private var correctAnswers  = 0
    // Количество попыток -
    // хранится общее количество правильных и неправильных ответов игрока до настоящего момента
    //private var totalGuesses   = 0
    // Количество строк с кнопками вариантов
    //private var guessRows = 0

    // Генератор случайных чисел
    private var random : SecureRandom = SecureRandom()

    private val  mToneGenerator: ToneGenerator by lazy{
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }

    //список стран из сети с именами, столицами, флагами
    //private var listStates: MutableList<State> = mutableListOf()

   // private var currentState: IFlagState = ReadyState(DataFlags())


    //здесь сбрасываем переменные и очищеем списки а также формируем список с необходимым
    // числом флагов для новой викторины
    override fun resetQuiz(dataFlags:DataFlags):DataFlags {
        dataFlags.correctAnswers = 0  // Сброс количества правильных ответов
        dataFlags.totalGuesses = 0 //  Сброс общего количества попыток
        dataFlags.quizCountriesList.clear()  // Очистка предыдущего списка стран

        val num = dataFlags.guessRows
        var flagCounter = 1
        //dataFlags.listStates - список с названиями стран, столицами, флагами
        val numberOfFlags = dataFlags.listStates.size
        // Добавление FLAGS_IN_QUIZ штук  случайных файлов в quizCountriesList
        while (flagCounter <= dataFlags.flagsInQuiz) {
            val randomIndex = random.nextInt(numberOfFlags)
            // Получение случайного элемента списка - экземпляра класса State
            val filename:State = dataFlags.listStates[randomIndex]
            // Если элемент списка еще не был выбран, добавляем его в список  для текущей викторины
            if (!dataFlags.quizCountriesList.contains(filename)) {
                dataFlags.quizCountriesList.add(filename)  // Добавить элемент в список для викторины
                ++flagCounter
            }
        }
      val nextDataFlags =   loadNextFlag(dataFlags) //загрузка новых данных для викторины
      return  nextDataFlags
    }

    //загрузка новых данных для викторины
    override fun loadNextFlag(dataFlags:DataFlags):DataFlags {
        // Получение названия следующей страны
        dataFlags.nextCountry = dataFlags.quizCountriesList.removeAt(0)
        //val nextImage: State = dataFlags.quizCountriesList.removeAt(0)
        dataFlags.correctAnswer = dataFlags.nextCountry?.nameRus // Обновление правильного ответа
        // Перестановка имен файлов - метод Котлин для коллекций
        dataFlags.listStates.shuffle()
        // Помещение правильного ответа в конец listStates - зачем?
        var correctIndex = 0
        for (i in 0 until dataFlags.listStates.size ){
            if(dataFlags.listStates[i].nameRus == dataFlags.correctAnswer){
                correctIndex = i
            }
        }
        // Помещение правильного ответа в конец listStates
        dataFlags.listStates.add(dataFlags.listStates.removeAt(correctIndex))

        ++dataFlags.correctAnswers //увеличиваем номер ответа

        return dataFlags
    }

    override fun updateSoundOnOff() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        val soung = sharedPreferences.getBoolean(MainActivity.SOUND, true)

        (app.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                .setStreamMute(AudioManager.STREAM_MUSIC, !soung)
    }

    override fun updateNumberFlagsInQuiz(dataFlags:DataFlags):DataFlags {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        val flagsNumber: String?  = sharedPreferences.getString(MainActivity.FLAGS_IN_QUIZ, 10.toString())
        flagsNumber?. let{
            dataFlags.flagsInQuiz =flagsNumber.toInt()
        }
        return dataFlags
    }

    override fun getGuessRows(dataFlags:DataFlags):DataFlags {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        // Получение количества отображаемых вариантов ответа
        val choices: String? = sharedPreferences.getString(MainActivity.CHOICES, 2.toString())
        choices?. let{
            dataFlags.guessRows = it.toInt() / 2
        }
            return dataFlags
    }

    override fun getTypeAnswer(guess:String, dataflags:DataFlags) :DataFlags {
        ++dataflags.totalGuesses  // Увеличение количества попыток пользователя

        // Если ответ правилен
        if (guess == dataflags.correctAnswer) {
            // Увеличить количество правильных ответов
            ++dataflags.correctAnswers
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