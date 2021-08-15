package com.bartex.quizday.ui.flags

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bartex.quizday.MainActivity
import com.bartex.quizday.R
import com.bartex.quizday.model.TestFlagClass
import com.bartex.quizday.ui.textquiz.TextQuizViewModel
import java.io.IOException
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList

class FlagsFragment: Fragment(), ResultDialog.OnResultListener {

    //todo
    // Количество флагов - для разработки - 2 , а вообще - 10 или задавать в настройках
   // private val FLAGS_IN_QUIZ = 2

    private var flagsInQuiz = 2
    // Имена файлов с флагами-
    //это имена файлов с изображениями флагов для текущего набора выбранных регионов
    //todo заменить Drawable на картинки svg из базы данных
    private var fileNameList  : MutableList<TestFlagClass> = ArrayList()
    // Страны текущей викторины -
    // переменная содержит имена файлов с флагами для стран, используемых в текущей игре
    //todo заменить на String или на State с потрохами
    private var quizCountriesList : MutableList<TestFlagClass> = ArrayList()
    // Регионы текущей викторины
    private var regionsSet  : Set<String>? = null

    //ViewModel фрагмента
    private lateinit var flagsViewModel: FlagsViewModel
    // Для задержки загрузки следующего флага
    private lateinit var handler : Handler
    // Макет с викториной
    private var quizLinearLayout  : LinearLayout? = null
    // Номер текущего вопроса
    private lateinit var questionNumberTextView  : TextView
    // Для правильного ответа
    private lateinit var answerTextView : TextView
    //ImageView  Для вывода флага
    private var flagImageView  : ImageView? = null
    // Строки с кнопками
    private var guessLinearLayouts : Array<LinearLayout?> = arrayOfNulls(3)
    // Правильная страна для текущего флага
    private var correctAnswer : String? = null
    // Количество правильных ответов -
    //если пользователь завершит викторину, это значение будет равно FLAGS_IN_QUIZ
    private var correctAnswers  = 0
    // Количество попыток -
    // хранится общее количество правильных и неправильных ответов игрока до настоящего момента
    private var totalGuesses   = 0
    // Количество строк с кнопками вариантов
    private var guessRows = 0
    // Генератор случайных чисел
    private var random : SecureRandom = SecureRandom()

    // Настройки изменились? При первом включении это вызывает запуск викторины
    private var preferencesChanged = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_flags, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flagsViewModel =
                ViewModelProvider(this).get(FlagsViewModel::class.java)

        handler  = Handler(requireActivity().mainLooper)

        // Получение ссылок на компоненты графического интерфейса
        quizLinearLayout = view.findViewById<View>(R.id.quizLinearLayout) as LinearLayout
        questionNumberTextView = view.findViewById<View>(R.id.questionNumberTextView) as TextView
        answerTextView = view.findViewById<View>(R.id.answerTextView) as TextView
        flagImageView = view.findViewById<View>(R.id.flagImageView) as ImageView

        //guessLinearLayouts = arrayOfNulls(3)
        guessLinearLayouts[0] = view.findViewById<View>(R.id.row1LinearLayout) as LinearLayout
        guessLinearLayouts[1] = view.findViewById<View>(R.id.row2LinearLayout) as LinearLayout
        guessLinearLayouts[2] = view.findViewById<View>(R.id.row3LinearLayout) as LinearLayout

        // Настройка слушателей для кнопок ответов
        //Перебираем строки в Array<LinearLayout?> - в каждой строке проходим
        // по всем детям LinearLayout, соторых считаем в row.childCount
        //В каждой строке находим кнопку по индексу колонки и устанавливаем слушатель
        for (row in guessLinearLayouts){
            for (column in 0 until row!!.childCount ){
                val button = row.getChildAt(column) as Button
                button.setOnClickListener(guessButtonListener)
            }
        }

        // Назначение текста questionNumberTextView
        questionNumberTextView.text = getString(R.string.question, 1, flagsInQuiz)

        //приводим меню тулбара в соответствии с onPrepareOptionsMenu в MainActivity
        //без этой строки меню в тулбаре ведёт себя неправильно
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    // метод onStart вызывается после onViewCreated.
    override fun onStart() {
        super.onStart()

        updateFlagsInQuiz(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        updateGuessRows(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        resetQuiz()
    }

    val guessButtonListener:   View.OnClickListener =   View.OnClickListener { v ->
        val guessButton = v as Button //нажатая кнопка ответа
        val guess = guessButton.text.toString() //ответ как текст на кнопке
        ++totalGuesses  // Увеличение количества попыток пользователя

        if (guess == correctAnswer) {  // Если ответ правилен
            ++correctAnswers  // Увеличить количество правильных ответов
            // Правильный ответ выводится зеленым цветом
            answerTextView.text = correctAnswer
            answerTextView.setTextColor(
                    ContextCompat.getColor(requireActivity(), R.color.correct_answer))

            disableButtons()  // Блокировка всех кнопок ответов
            if (correctAnswers == flagsInQuiz) {
                // DialogFragment для вывода статистики и перезапуска
                //в отдельном файле сделан, а не внутри фрагмента
                ResultDialog(flagsInQuiz, totalGuesses, this )
                        .show(requireActivity().supportFragmentManager, "ResultDialog")

            }else { // Ответ правильный, но викторина не закончена
                    // Загрузка следующего флага после двухсекундной задержки
                    handler.postDelayed(
                        {
                        //todo сделать
                            //animate(true)  // Анимация исчезновения флага
                            loadNextFlag()  //если без анимации
                        }, 1000
                    ) // 2000 миллисекунд для двухсекундной задержки
                }
            }else { // Неправильный ответ
                //todo Встряхивание сделать
            //flagImageView!!.startAnimation(shakeAnimation)  // Встряхивание

            // Сообщение "Неправильно!" выводится красным шрифтом
            answerTextView.setText(R.string.incorrect_answer)
            answerTextView.setTextColor(
                    ContextCompat.getColor(requireActivity(), R.color.correct_answer))
            guessButton.isEnabled = false  // Блокировка неправильного ответа
        }
    }

    fun disableButtons(){
        for (row in guessLinearLayouts){
            for (column in 0 until row!!.childCount ){
                val button = row.getChildAt(column) as Button
                button.isEnabled = false
            }
        }
    }
    private fun updateFlagsInQuiz(sharedPreferences: SharedPreferences) {
     val flagsNumber: String?  = sharedPreferences.getString(MainActivity.FLAGS_IN_QUIZ, 10.toString())
        flagsNumber?. let{
           // flagsInQuiz  = it.toInt()
            flagsInQuiz =flagsNumber.toInt()
        }
    }

    // Обновление guessRows на основании значения SharedPreferences
    fun updateGuessRows(sharedPreferences: SharedPreferences) {
        // Получение количества отображаемых вариантов ответа
        val choices: String? = sharedPreferences.getString(MainActivity.CHOICES, 2.toString())
        choices?. let{
            guessRows = it.toInt() / 2
        }
        // Сначала все компоненты LinearLayout скрываются
        for (layout in guessLinearLayouts){
            layout?.visibility = View.GONE
        }
        // Отображение нужных компонентов LinearLayout
        for (row in 0 until guessRows) {
            guessLinearLayouts[row]?.visibility = View.VISIBLE
        }
    }

    // Настройка и запуск следующей серии вопросов
    override fun resetQuiz() {
        //todo потом заменить на список с изображениями флагов
        //получаем список из 10 одинаковых картинок с разными именами
        fileNameList = flagsViewModel.getFlags()

        correctAnswers = 0  // Сброс количества правильных ответов
        totalGuesses = 0 //  Сброс общего количества попыток
        quizCountriesList.clear()  // Очистка предыдущего списка стран

        var flagCounter = 1
        val numberOfFlags = fileNameList.size
        // Добавление FLAGS_IN_QUIZ штук  случайных файлов в quizCountriesList
        while (flagCounter <= flagsInQuiz) {
            val randomIndex = random.nextInt(numberOfFlags)
            // Получение случайного имени файла
            val filename: TestFlagClass = fileNameList[randomIndex]
            // Если файл еще не был выбран, добавляем его в список файлов для викторины
            if (!quizCountriesList.contains(filename)) {
                quizCountriesList.add(filename)  // Добавить файл в список файлов для викторины
                ++flagCounter
            }
        }
        loadNextFlag() // Запустить викторину загрузкой первого флага
    }

    // Загрузка следующего флага после правильного ответа
    private fun loadNextFlag() {
        // Получение имени файла следующего флага и удаление его из списка
        val nextImage: TestFlagClass = quizCountriesList.removeAt(0)
        correctAnswer = nextImage.name // Обновление правильного ответа
        answerTextView.text = "" // Очистка answerTextView

        // Отображение номера текущего вопроса
        questionNumberTextView.text = getString(
                R.string.question, correctAnswers + 1, flagsInQuiz
        )

        flagImageView?.setImageDrawable(nextImage.image)

        Collections.shuffle(fileNameList) // Перестановка имен файлов

        // Помещение правильного ответа в конец fileNameList - зачем?
        var correctIndex = 0
        for (i in 0 until fileNameList.size ){
          if(fileNameList[i].name == correctAnswer){
              correctIndex = i
          }
        }
        // Помещение правильного ответа в конец fileNameList
        fileNameList.add(fileNameList.removeAt(correctIndex))

        // Добавление 2, 4, 6 кнопок в зависимости от значения guessRows
        for (row in 0 until guessRows) {
            // Размещение кнопок в currentTableRow
            for (column in 0 until guessLinearLayouts[row]!!.childCount) {
                // Получение ссылки на Button
                val newGuessButton = guessLinearLayouts[row]!!.getChildAt(column) as Button
                newGuessButton.isEnabled = true

                // Назначение названия страны текстом newGuessButton
                val filename = fileNameList[row * 2 + column].name
                newGuessButton.text = filename
            }
        }

        // Случайная замена одной кнопки правильным ответом
        val row = random.nextInt(guessRows) // Выбор случайной строки
        val column = random.nextInt(2) // Выбор случайного столбца
        val randomRow = guessLinearLayouts[row] // Получение строки

        (randomRow!!.getChildAt(column) as Button).text = correctAnswer
    }

}