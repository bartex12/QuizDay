package com.bartex.quizday.ui.flags

import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.SyncStateContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bartex.quizday.MainActivity
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.model.fsm.substates.*
import com.bartex.quizday.room.Database
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import java.security.SecureRandom

class FlagsFragment: Fragment(), ResultDialog.OnResultListener {

    private val flagsViewModel by lazy{
        ViewModelProvider(this).get(FlagsViewModel::class.java)
    }
    private val  mToneGenerator: ToneGenerator by lazy{
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }
    private var regionsSet  : Set<String>? = null   // Регионы текущей викторины
    private lateinit var handler : Handler   // Для задержки загрузки следующего флага
    private lateinit var quizLinearLayout  : LinearLayout // root макета фрагмента
    private lateinit var questionNumberTextView  : TextView   //для номера текущего вопроса
    private lateinit var answerTextView : TextView  //для правильного ответа
    private lateinit var flagImageView  : ImageView  //Для вывода флага
    private lateinit var guessButton:Button  //кнопка ответа
    private lateinit var progressBarFlags:ProgressBar
    private var guessLinearLayouts : Array<LinearLayout?> = arrayOfNulls(3) //кнопки ответов
    private var random : SecureRandom = SecureRandom()
    private var currentState: IFlagState = ReadyState(DataFlags())
    private var listStates:MutableList<State> = mutableListOf()
    //для доступа к полю MainActivity isNetworkAvailable, где проверяется доступ к интернету
    lateinit var main:MainActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        main = requireActivity() as MainActivity
            return inflater.inflate(R.layout.fragment_flags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHandler()
        initViews(view)
        initButtonsListeners()
        initMenu()

        val  isNetworkAvailable = main.getNetworkAvailable()
        //получаем страны из сети и после этого запускаем викторину
        flagsViewModel.getStatesSealed(isNetworkAvailable)
            .observe(viewLifecycleOwner,  {
                renderData(it)
            })

        //следим за состоянием конечного автомата
        flagsViewModel.getCurrentState()
                .observe(viewLifecycleOwner, {newQuizState->
                    currentState = newQuizState
                    renderViewState(newQuizState)
                })
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
            is NextFlagState -> showNextFlagState(newQuizState.data)
            is NotWellAnswerState -> showNotWellState()
            is WellNotLastAnswerState -> showWellNotLastState(newQuizState.data)
            is WellAndLastAnswerState -> showWellAndLastState(newQuizState.data)
        }
    }

    private fun showReadyState(data: DataFlags) {
        flagsViewModel.loadFirstFlag(currentState, data)
    }

    private fun showNextFlagState(data: DataFlags) {
        answerTextView.text = ""
        questionNumberTextView.text = getString(
                R.string.question, data.correctAnswers, data.flagsInQuiz //текущий вопрос
        )
        //svg изображение флага
        data.nextCountry?.flag?. let{flag->
            GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(flag), flagImageView)
        }
        // Добавление 2, 4, 6 кнопок в зависимости от значения guessRows
        for (row in 0 until data.guessRows) {
            // Размещение кнопок в currentTableRow
            for (column in 0 until guessLinearLayouts[row]!!.childCount) {
                // Получение ссылки на Button
                val newGuessButton = guessLinearLayouts[row]!!.getChildAt(column) as Button
                newGuessButton.isEnabled = true //так как при неправильном ответе было false
                // названия страны как текст кнопки
                val buttonName = data.listStates[row * 2 + column].nameRus
                newGuessButton.text = buttonName
            }
        }
        // Случайная замена одной кнопки правильным ответом
        val row = random.nextInt(data.guessRows) // Выбор случайной строки
        val column = random.nextInt(2) // Выбор случайного столбца
        val randomRow = guessLinearLayouts[row] // Получение строки

        (randomRow?.getChildAt(column) as Button).text = data.correctAnswer
    }

    //ответ неправильный
    private fun showNotWellState() {
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_L, 100) }.start()
        //todo анимацию встряхивания сделать
        answerTextView.setText(R.string.incorrect_answer)
        answerTextView.setTextColor(
                ContextCompat.getColor(requireActivity(), R.color.incorrect_answer))
        guessButton.isEnabled = false  // Блокировка неправильного ответа
    }

    // Ответ правильный, но викторина не закончена
    private fun showWellNotLastState(data: DataFlags) {
        answerTextView.text = data.correctAnswer
        answerTextView.setTextColor(
                ContextCompat.getColor(requireActivity(), R.color.correct_answer))
        disableButtons()  // Блокировка всех кнопок ответов
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 50) }.start()
        handler.postDelayed(
                { //todo сделать анимацию исчезновения флага
                    flagsViewModel.loadNextFlag(currentState, data)
                }, 1000
        )
    }

    // Ответ правильный и викторина закончена
    private fun showWellAndLastState(data: DataFlags) {
        answerTextView.text = data.correctAnswer
        answerTextView.setTextColor(
                ContextCompat.getColor(requireActivity(), R.color.correct_answer))
        disableButtons()
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 100) }.start()
        //для вывода статистики и перезапуска
        ResultDialog(data.flagsInQuiz, data.totalGuesses, this )
                .show(requireActivity().supportFragmentManager, "ResultDialog")
    }

    private fun initHandler() {
        handler = Handler(requireActivity().mainLooper)
    }

    private fun initViews(view: View) {

        quizLinearLayout = view.findViewById<View>(R.id.quizLinearLayout) as LinearLayout
        questionNumberTextView = view.findViewById<View>(R.id.questionNumberTextView) as TextView
        answerTextView = view.findViewById<View>(R.id.answerTextView) as TextView
        flagImageView = view.findViewById<View>(R.id.flagImageView) as ImageView
        progressBarFlags = view.findViewById<View>(R.id.progressBarFlags) as ProgressBar

        guessLinearLayouts[0] = view.findViewById<View>(R.id.row1LinearLayout) as LinearLayout
        guessLinearLayouts[1] = view.findViewById<View>(R.id.row2LinearLayout) as LinearLayout
        guessLinearLayouts[2] = view.findViewById<View>(R.id.row3LinearLayout) as LinearLayout
    }

    private fun initButtonsListeners() {
        //Перебираем строки в Array<LinearLayout?> - в каждой строке проходим
        // по всем детям LinearLayout, соторых считаем в row.childCount
        //В каждой строке находим кнопку по индексу колонки и устанавливаем слушатель
        for (row in guessLinearLayouts) {
            for (column in 0 until row!!.childCount) {
                val button = row.getChildAt(column) as Button
                button.setOnClickListener(guessButtonListener)
            }
        }
    }

    private fun initMenu() {
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    private fun renderData(data: StatesSealed?) {
        when(data){
            is StatesSealed.Success -> {
                //показываем макет викторины, скрываем прогресс
                quizLinearLayout.visibility = View.VISIBLE
                progressBarFlags.visibility = View.GONE
                //список стран с названиями, столицами, флагами
                listStates = data.states as MutableList<State>
                flagsViewModel.resetQuiz(listStates)
            }
            is StatesSealed.Error ->{
                Toast.makeText(requireActivity(), "${data.error.message}", Toast.LENGTH_SHORT).show()
            }
            is StatesSealed.Loading ->{
                quizLinearLayout.visibility = View.GONE
                progressBarFlags.visibility = View.VISIBLE
            }
        }
    }

    private val guessButtonListener:   View.OnClickListener =   View.OnClickListener { v ->
       // guessButton = v as Button //нажатая кнопка ответа
        guessButton = when(v.id){
            R.id.button -> v.findViewById(R.id.button)
            R.id.button2 -> v.findViewById(R.id.button2)
            R.id.button3 -> v.findViewById(R.id.button3)
            R.id.button4 -> v.findViewById(R.id.button4)
            R.id.button5 -> v.findViewById(R.id.button5)
            R.id.button6 -> v.findViewById(R.id.button6)
            else -> v.findViewById(R.id.button)
        }
        val guess = guessButton.text.toString() //ответ как текст на кнопке
        flagsViewModel.answer(currentState, guess) //определить тип ответа
    }

    private fun disableButtons(){
        for (row in guessLinearLayouts){
            for (column in 0 until row!!.childCount ){
                val button = row.getChildAt(column) as Button
                button.isEnabled = false
            }
        }
    }

    // Обновление guessRows на основании значения SharedPreferences
    private fun updateGuessRows(guessRows:Int) {
        // Сначала все компоненты LinearLayout скрываются
        for (layout in guessLinearLayouts){
            layout?.visibility = View.GONE
        }
        // Отображение нужных компонентов LinearLayout
        for (row in 0 until guessRows) {
            guessLinearLayouts[row]?.visibility = View.VISIBLE
        }
    }

    //прилетает из ResultDialog
    override fun resetQuiz() {
        flagsViewModel.resetQuiz(listStates)
    }

}