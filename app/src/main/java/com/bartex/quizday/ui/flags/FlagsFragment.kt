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
import com.bartex.quizday.model.fsm.model.DataFlags
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.substates.*
import com.bartex.quizday.utils.Util
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import java.security.SecureRandom
import kotlin.collections.ArrayList

class FlagsFragment: Fragment(), ResultDialog.OnResultListener {


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
    // Генератор случайных чисел
    private var random : SecureRandom = SecureRandom()

    private val  mToneGenerator: ToneGenerator by lazy{
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }

    private var currentState: IFlagState = ReadyState(DataFlags())

    lateinit var guessButton:Button

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //если аргументы не передали, то будет ReadyState(DataFlags())
//        arguments?.let {
//            currentState = it.getSerializable(Util.STATE) as IFlagState
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initHandler()

        //загрузка стран - название, столица, флаг
        flagsViewModel.loadDataSealed()
        //наблюдаем за классом StatesSealed, в которам нас интересует class Success(val state:List<State>)
        flagsViewModel.getStatesSealed()
            .observe(viewLifecycleOwner, Observer<StatesSealed> {
                renderData(it) //здесь после получения данных запускаем викторину
            })

        flagsViewModel.getCurrentState()
                .observe(viewLifecycleOwner, {newQuizState->
                    currentState = newQuizState //запоминаем в переменной
                    renderViewState(newQuizState)
                })

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

        //приводим меню тулбара в соответствии с onPrepareOptionsMenu в MainActivity
        //без этой строки меню в тулбаре ведёт себя неправильно
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    // метод onStart вызывается после onViewCreated.
    override fun onStart() {
        super.onStart()

        flagsViewModel.updateSoundOnOff() //обновляем звук
        flagsViewModel.updateNumberFlagsInQuiz() //обновляем число вопросов в викторине
        updateGuessRows(flagsViewModel.getGuessRows()) //обновляем число выриантов ответов в викторине
    }

    private fun renderViewState(newQuizState: IFlagState) {
        when (newQuizState) {
            is ReadyState -> showReadyState(newQuizState.data)
            is NotWellState -> showNotWellState(newQuizState.data)
            is WellNotLastState -> showWellNotLastState(newQuizState.data)
            is WellAndLastState -> showWellAndLastState(newQuizState.data)
        }
    }



    private fun showReadyState(data: DataFlags) {
        answerTextView.text = "" // Очистка answerTextView
        // Отображение номера текущего вопроса
        questionNumberTextView.text = getString(
                R.string.question, data.correctAnswers, data.flagsInQuiz
        )

        //загружаем svg изображение флага
        data.nextCountry?.flag?. let{flag->
            GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(flag), flagImageView)
        }
        // Добавление 2, 4, 6 кнопок в зависимости от значения guessRows
        for (row in 0 until data.guessRows) {
            // Размещение кнопок в currentTableRow
            for (column in 0 until guessLinearLayouts[row]!!.childCount) {
                // Получение ссылки на Button
                val newGuessButton = guessLinearLayouts[row]!!.getChildAt(column) as Button
                newGuessButton.isEnabled = true //так как при правильном ответе было false

                // Назначение названия страны текстом newGuessButton
                val filename = data.listStates[row * 2 + column].nameRus
                newGuessButton.text = filename
            }
        }

        // Случайная замена одной кнопки правильным ответом
        val row = random.nextInt(data.guessRows) // Выбор случайной строки
        val column = random.nextInt(2) // Выбор случайного столбца
        val randomRow = guessLinearLayouts[row] // Получение строки

        (randomRow!!.getChildAt(column) as Button).text = data.correctAnswer
    }

    private fun showNotWellState(data: DataFlags) {
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

    private fun showWellNotLastState(data: DataFlags) {
        answerTextView.text = data.correctAnswer
        answerTextView.setTextColor(
                ContextCompat.getColor(requireActivity(), R.color.correct_answer))

        disableButtons()  // Блокировка всех кнопок ответов

        // Ответ правильный, но викторина не закончена
        // Загрузка следующего флага после двухсекундной задержки
        //в новом потоке чтобы не было задержек времени
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 50) }.start()
        handler.postDelayed(
                {
                    //todo сделать
                    //animate(true)  // Анимация исчезновения флага
                   flagsViewModel.loadNextFlag(data)  //если без анимации
                }, 1000
        )
    }

    private fun showWellAndLastState(data: DataFlags) {
        answerTextView.text = data.correctAnswer
        answerTextView.setTextColor(
                ContextCompat.getColor(requireActivity(), R.color.correct_answer))

        disableButtons()  // Блокировка всех кнопок ответов

        //в новом потоке чтобы не было задержек времени
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 100) }.start()
        // DialogFragment для вывода статистики и перезапуска
        //в отдельном файле сделан, а не внутри фрагмента
        ResultDialog(data.flagsInQuiz, data.totalGuesses, this )
                .show(requireActivity().supportFragmentManager, "ResultDialog")
    }

    private fun initHandler() {
        handler = Handler(requireActivity().mainLooper)
    }

    private fun initViews(view: View) {
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
    }

    private fun renderData(data: StatesSealed?) {

        when(data){
            is StatesSealed.Success -> {
                //показываем макет викторины, скрываем прогресс
                quizLinearLayout?.visibility = View.VISIBLE
                progressBarFlags?.visibility = View.GONE
                //список стран с именами, столицами, флагами
                val  listStates = data.state as MutableList<State>
                Toast.makeText(requireActivity(), "Количество стран = ${listStates.size}", Toast.LENGTH_SHORT).show()
                flagsViewModel.resetQuiz(listStates)
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
        guessButton = v as Button //нажатая кнопка ответа
        val guess = guessButton.text.toString() //ответ как текст на кнопке

        flagsViewModel.answer(currentState, guess)
    }

    fun disableButtons(){
        for (row in guessLinearLayouts){
            for (column in 0 until row!!.childCount ){
                val button = row.getChildAt(column) as Button
                button.isEnabled = false
            }
        }
    }

    // Обновление guessRows на основании значения SharedPreferences
    fun updateGuessRows(guessRows:Int) {
        // Сначала все компоненты LinearLayout скрываются
        for (layout in guessLinearLayouts){
            layout?.visibility = View.GONE
        }
        // Отображение нужных компонентов LinearLayout
        for (row in 0 until guessRows) {
            guessLinearLayouts[row]?.visibility = View.VISIBLE
        }
    }

    override fun resetQuiz() {
        //flagsViewModel.resetQuiz(ReadyState(DataFlags()), listStates)
    }
}