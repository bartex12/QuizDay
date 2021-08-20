package com.bartex.quizday.ui.flags

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import com.bartex.quizday.model.entity.State
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList

class FlagsFragment: Fragment(), ResultDialog.OnResultListener {

    private var flagsInQuiz = 2
    // Имена файлов с флагами-
    //это имена файлов с изображениями флагов для текущего набора выбранных регионов
    //todo заменить Drawable на картинки svg из базы данных
    //private var fileNameList  : MutableList<TestFlagClass> = ArrayList()
    // Страны текущей викторины -
    // переменная содержит имена файлов с флагами для стран, используемых в текущей игре
    //todo заменить на String или на State с потрохами
   // private var quizCountriesList : MutableList<TestFlagClass> = ArrayList()
    private var quizCountriesList : MutableList<State> = ArrayList()
    // Регионы текущей викторины
    private var regionsSet  : Set<String>? = null

    //ViewModel фрагмента
    private val flagsViewModel by lazy{
        ViewModelProvider(this).get(FlagsViewModel::class.java)
    }
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
    //прогресс бар на время загрузки
    private var progressBarFlags:ProgressBar? = null
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

    private val  mToneGenerator: ToneGenerator by lazy{
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }

    //список стран из сети с именами, столицами, флагами
    private var listStates: MutableList<State> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //загрузка стран - название, столица, флаг
        flagsViewModel.loadDataSealed()
        //наблюдаем за классом StatesSealed, в которам нас интересует class Success(val state:List<State>)
        flagsViewModel.getStatesSealed()
            .observe(viewLifecycleOwner, Observer<StatesSealed> {
                renderData(it)
            })

        handler  = Handler(requireActivity().mainLooper)

        // Получение ссылок на компоненты графического интерфейса
        quizLinearLayout = view.findViewById<View>(R.id.quizLinearLayout) as LinearLayout
        questionNumberTextView = view.findViewById<View>(R.id.questionNumberTextView) as TextView
        answerTextView = view.findViewById<View>(R.id.answerTextView) as TextView
        flagImageView = view.findViewById<View>(R.id.flagImageView) as ImageView
        progressBarFlags = view.findViewById<View>(R.id.progressBarFlags) as ProgressBar

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
        updateSoundOnOff(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        updateNumberFlagsInQuiz(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        //две строчки ниже делаем после получения данных из сети
        //updateGuessRows(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        //resetQuiz()
    }

    private fun renderData(data: StatesSealed?) {

        when(data){
            is StatesSealed.Success -> {
                //показываем макет викторины, скрываем прогресс
                quizLinearLayout?.visibility = View.VISIBLE
                progressBarFlags?.visibility = View.GONE
                //список стран с именами, столицами, флагами
                listStates = data.state as MutableList<State>
                Toast.makeText(requireActivity(), "Количество стран = ${listStates.size}", Toast.LENGTH_SHORT).show()
                //обновляем количество кнопок с ответами
                updateGuessRows(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
                //Настройка и запуск следующей серии вопросов
                resetQuiz()
            }
            is StatesSealed.Error ->{
                Toast.makeText(requireActivity(), "${data.error.message}", Toast.LENGTH_SHORT).show()
            }
            is StatesSealed.Loading ->{
                //показываем прогресс, скрываем макет викторины
                quizLinearLayout?.visibility = View.GONE
                progressBarFlags?.visibility = View.VISIBLE
            }
        }
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
                //в новом потоке чтобы не было задержек времени
                Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 100) }.start()
                // DialogFragment для вывода статистики и перезапуска
                //в отдельном файле сделан, а не внутри фрагмента
                ResultDialog(flagsInQuiz, totalGuesses, this )
                        .show(requireActivity().supportFragmentManager, "ResultDialog")

            }else { // Ответ правильный, но викторина не закончена
                    // Загрузка следующего флага после двухсекундной задержки
                //в новом потоке чтобы не было задержек времени
                Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 50) }.start()
                    handler.postDelayed(
                        {
                        //todo сделать
                            //animate(true)  // Анимация исчезновения флага
                            loadNextFlag()  //если без анимации
                        }, 1000
                    ) // 2000 миллисекунд для двухсекундной задержки
                }
            }else { // Неправильный ответ
            //в новом потоке чтобы не было задержек времени
            Thread { mToneGenerator.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_L, 100) }.start()
                //todo Встряхивание сделать
            //flagImageView!!.startAnimation(shakeAnimation)  // Встряхивание

            // Сообщение "Неправильно!" выводится красным шрифтом
            answerTextView.setText(R.string.incorrect_answer)
            answerTextView.setTextColor(
                    ContextCompat.getColor(requireActivity(), R.color.incorrect_answer))
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
    private fun updateSoundOnOff(sharedPreferences: SharedPreferences) {
        val soung = sharedPreferences.getBoolean(MainActivity.SOUND, true)

        (requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        .setStreamMute(AudioManager.STREAM_MUSIC, !soung)
    }

    private fun updateNumberFlagsInQuiz(sharedPreferences: SharedPreferences) {
     val flagsNumber: String?  = sharedPreferences.getString(MainActivity.FLAGS_IN_QUIZ, 10.toString())
        flagsNumber?. let{
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
        //fileNameList = flagsViewModel.getFlags()

        correctAnswers = 0  // Сброс количества правильных ответов
        totalGuesses = 0 //  Сброс общего количества попыток
        quizCountriesList.clear()  // Очистка предыдущего списка стран

        var flagCounter = 1
        //listStates - список с названиями стран, столицами, флагами
        val numberOfFlags = listStates.size
        // Добавление FLAGS_IN_QUIZ штук  случайных файлов в quizCountriesList
        while (flagCounter <= flagsInQuiz) {
            val randomIndex = random.nextInt(numberOfFlags)
            // Получение случайного имени файла
            //val filename: TestFlagClass = fileNameList[randomIndex]
            // Получение случайного элемента списка - экземпляра класса State
            val filename:State = listStates[randomIndex]
            // Если элемент списка еще не был выбран, добавляем его в список  для текущей викторины
            if (!quizCountriesList.contains(filename)) {
                if (filename.nameRus == "Unknown")return //чтобы не попадали такие названия
                quizCountriesList.add(filename)  // Добавить элемент в список для викторины
                ++flagCounter
            }
        }
        loadNextFlag() // Запустить викторину загрузкой первого флага
    }

    // Загрузка следующего флага после правильного ответа
    private fun loadNextFlag() {
        // Получение имени файла следующего флага и удаление его из списка
        //val nextImage: TestFlagClass = quizCountriesList.removeAt(0)
        val nextImage: State = quizCountriesList.removeAt(0)
        correctAnswer = nextImage.nameRus // Обновление правильного ответа
        answerTextView.text = "" // Очистка answerTextView

        // Отображение номера текущего вопроса
        questionNumberTextView.text = getString(
                R.string.question, correctAnswers + 1, flagsInQuiz
        )

        //загружаем svg изображение флага
        nextImage.flag?. let{flag->
            GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(flag), flagImageView)
        }
       // flagImageView?.setImageDrawable(nextImage.image)

        // Перестановка имен файлов - метод Котлин для коллекций
        listStates.shuffle()

        // Помещение правильного ответа в конец listStates - зачем?
        var correctIndex = 0
        for (i in 0 until listStates.size ){
          if(listStates[i].nameRus == correctAnswer){
              correctIndex = i
          }
        }
        // Помещение правильного ответа в конец listStates
        listStates.add(listStates.removeAt(correctIndex))

        // Добавление 2, 4, 6 кнопок в зависимости от значения guessRows
        for (row in 0 until guessRows) {
            // Размещение кнопок в currentTableRow
            for (column in 0 until guessLinearLayouts[row]!!.childCount) {
                // Получение ссылки на Button
                val newGuessButton = guessLinearLayouts[row]!!.getChildAt(column) as Button
                newGuessButton.isEnabled = true //так как при правильном ответе было false

                // Назначение названия страны текстом newGuessButton
                val filename = listStates[row * 2 + column].nameRus
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