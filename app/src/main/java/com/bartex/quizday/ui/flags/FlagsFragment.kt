package com.bartex.quizday.ui.flags

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bartex.quizday.MainActivity
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.model.fsm.substates.*
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.android.material.chip.ChipGroup
import java.security.SecureRandom

class FlagsFragment: Fragment(){

    companion object{
        const val TAG = "33333"
    }

    private val flagsViewModel by lazy{
        ViewModelProvider(requireActivity()).get(FlagsViewModel::class.java)
    }
    private val  mToneGenerator: ToneGenerator by lazy{
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }

    private lateinit var handler : Handler   // Для задержки загрузки следующего флага
    private lateinit var quizLinearLayout  : LinearLayout // root макета фрагмента
    private lateinit var answerTextView : TextView  //для правильного ответа
    private lateinit var flagImageView  : ImageView  //Для вывода флага
    private lateinit var guessButton:Button  // текущая кнопка ответа
    private lateinit var progressBarFlags:ProgressBar
    private var guessLinearLayouts : Array<LinearLayout?> = arrayOfNulls(3) //кнопки ответов
    private var random : SecureRandom = SecureRandom()
    private lateinit var chipGroup:ChipGroup
    private lateinit var navController:NavController
    private var listener:OnChangeToolbarTitleListener? = null

    interface OnChangeToolbarTitleListener{
        fun onChangeToolbarTitle(title:String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener  =  context as OnChangeToolbarTitleListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_flags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        initHandler()
        initViews(view)
        initChipGroupListener()
        initButtonsListeners()
        initMenu()

        val  isNetworkAvailable = (requireActivity() as MainActivity).getNetworkAvailable()

        //при первом обращении получаем список стран из сети , при поворотах экрана - из ViewModel
        if (savedInstanceState == null){
            //получаем страны из сети и после этого запускаем викторину
            flagsViewModel.getStatesSealed(isNetworkAvailable)
                    .observe(viewLifecycleOwner,  {
                        renderData(it)
                    })

            //выделение на Европу при перврй загрузке
            chipGroup.check(R.id.chip_Europa)
        }

        //следим за состоянием конечного автомата
        flagsViewModel.getCurrentState()
                .observe(viewLifecycleOwner, { newQuizState ->
                    flagsViewModel.saveCurrentState(newQuizState)
                    renderViewState(newQuizState)
                    Log.d(TAG, "FlagsFragment onViewCreated: newQuizState = $newQuizState")
                })
    }

    // метод onStart вызывается после onViewCreated.
    override fun onStart() {
        super.onStart()
        flagsViewModel.updateSoundOnOff() //обновляем звук
        flagsViewModel.updateNumberFlagsInQuiz() //обновляем число вопросов в викторине
        updateGuessRows(flagsViewModel.getGuessRows()) //обновляем число выриантов ответов в викторине
    }

    private fun initChipGroupListener() {

        chipGroup.setOnCheckedChangeListener { _, id ->
            val newRegion:String = when (id) {
                R.id.chip_all-> Constants.REGION_ALL
                R.id.chip_Europa -> Constants.REGION_EUROPE
                R.id.chip_Asia -> Constants.REGION_ASIA
                R.id.chip_America-> Constants.REGION_AMERICAS
                R.id.chip_Oceania -> Constants.REGION_OCEANIA
                R.id.chip_Africa -> Constants.REGION_AFRICA
                else -> Constants.REGION_EUROPE
            }
            if (newRegion != flagsViewModel.getRegion()){
                flagsViewModel.saveRegion(newRegion)
                flagsViewModel.resetQuiz()
            }
        }
    }

    private fun renderViewState(newQuizState: IFlagState) {
        when (newQuizState) {
            is ReadyState -> showReadyState(newQuizState.data)
            is NextFlagState -> showNextFlagState(newQuizState.data)
            is NotWellAnswerState -> showNotWellState(newQuizState.data)
            is WellNotLastAnswerState -> showWellNotLastState(newQuizState.data)
            is WellAndLastAnswerState -> showWellAndLastState(newQuizState.data)
        }
    }

    //состояние готовности к викторине - показываем первый флаг
    private fun showReadyState(data: DataFlags) {
        flagsViewModel.loadNextFlag(data)
    }

    //следующий вопрос
    private fun showNextFlagState(data: DataFlags) {
        listener?.onChangeToolbarTitle(getCurrentQuestion(data))//показать номер текущего вопроса
        answerTextView.text = "" //не показывать пока ответ
        showNextCountryFlag(data)  //svg изображение флага data
        showAnswerButtonsNumberAndNames(data)// Добавление кнопок
        //выбираем случайную строку и запоминаем её  в классе данных, чтобы не потерять при повороте
        data.row = random.nextInt(data.guessRows)
        //выбираем случайный столбец и запоминаем его  в классе данных, чтобы не потерять при повороте
        data.column = random.nextInt(2)
        showCorrectAnswerButtom(data)
    }

    //неправильный ответ
    private fun showNotWellState(data: DataFlags) {
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_L, 100) }.start()
        listener?.onChangeToolbarTitle(getCurrentQuestion(data))//показать номер текущего вопроса
        showIncorrectAnswer()//показать неправильный ответ
        //todo анимацию встряхивания сделать
        showNextCountryFlag(data) //svg изображение флага
        showAnswerButtonsNumberAndNames(data) // Добавление кнопок
        showCorrectAnswerButtom(data)
    }

    // Ответ правильный, но викторина не закончена
    private fun showWellNotLastState(data: DataFlags) {
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 50) }.start()
        listener?.onChangeToolbarTitle(getCurrentQuestion(data)) //показать номер текущего вопроса
        showCorrectAnswer(data) //показать правильный ответ
        disableButtons()  // Блокировка всех кнопок ответов
        handler.postDelayed(
                { //todo сделать анимацию исчезновения флага
                    flagsViewModel.loadNextFlag(data)
                }, 1000
        )
    }

    // Ответ правильный и викторина закончена
    private fun showWellAndLastState(data: DataFlags) {
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 100) }.start()
        listener?.onChangeToolbarTitle(getCurrentQuestion(data)) //показать номер текущего вопроса
        showCorrectAnswer(data) //показать правильный ответ
        disableButtons() //сделать иконки недоступными
        showNextCountryFlag(data)  //svg изображение флага

        //если диалог не создан - создаём и передаём данные
        if(flagsViewModel.isNeedToCreateDialog()){
            val bundle = Bundle()
            bundle. putInt(Constants.TOTAL_QUESTIONS, data.flagsInQuiz )
            bundle. putInt(Constants.TOTAL_GUESSES, data.totalGuesses )
            navController.navigate(R.id.resultDialog, bundle)
        }
    }

    private fun getCurrentQuestion(data: DataFlags):String {
        return getString( R.string.question, data.correctAnswers, data.flagsInQuiz)
    }

    private fun showNextCountryFlag(data: DataFlags) {
        data.nextCountry?.flag?.let { flag ->
            GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(flag), flagImageView)
        }
    }

    private fun showCorrectAnswerButtom(data: DataFlags) {
        // Получение строки LinearLayouts
        val randomRow = guessLinearLayouts[data.row]
        // Случайная замена одной кнопки правильным ответом
        (randomRow?.getChildAt(data.column) as Button).text = data.correctAnswer
    }

    // Добавление 2, 4, 6 кнопок в зависимости от значения guessRows
    private fun showAnswerButtonsNumberAndNames(data: DataFlags) {
        for (row in 0 until data.guessRows) {
            // Размещение кнопок в currentTableRow
            for (column in 0 until guessLinearLayouts[row]!!.childCount) {
                // Получение ссылки на Button
                val newGuessButton = guessLinearLayouts[row]!!.getChildAt(column) as Button
                // названия страны как текст кнопки - первые строчки списка который был перемешан
                val buttonName = data.listStates[row * 2 + column].nameRus
                newGuessButton.text = buttonName
                //доступность в зависимости от содержания в списке неправильных ответов
                newGuessButton.isEnabled = !data.buttonNotWellAnswerList.contains(buttonName)
            }
        }
    }

    private fun showIncorrectAnswer() {
        answerTextView.setText(R.string.incorrect_answer)
        answerTextView.setTextColor(
                ContextCompat.getColor(requireActivity(), R.color.incorrect_answer))
    }

    private fun showCorrectAnswer(data: DataFlags) {
        answerTextView.text = data.correctAnswer
        answerTextView.setTextColor(
                ContextCompat.getColor(requireActivity(),  R.color.correct_answer))
    }

    private fun initHandler() {
        handler = Handler(requireActivity().mainLooper)
    }

    private fun initViews(view: View) {

        chipGroup =view.findViewById<ChipGroup>(R.id.chip_region)

        quizLinearLayout = view.findViewById<View>(R.id.quizLinearLayout) as LinearLayout
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
                val  listStates = data.states as MutableList<State>
                //сохраняем список стран во ViewModel на время жизни фрагмента
                flagsViewModel.saveListOfStates(listStates)
                //переводим конечный автомат в состояние ReadyState
                flagsViewModel.resetQuiz()
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
       guessButton = v as Button //нажатая кнопка ответа
        val guess = guessButton.text.toString() //ответ как текст на кнопке
        flagsViewModel.answer(guess) //определить тип ответа
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
}