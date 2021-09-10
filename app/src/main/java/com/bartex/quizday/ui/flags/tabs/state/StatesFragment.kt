package com.bartex.quizday.ui.flags.tabs.state

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bartex.quizday.MainActivity
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.entity.ButtonTag
import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.model.fsm.substates.*
import com.bartex.quizday.network.NoInternetDialogFragment
import com.bartex.quizday.ui.flags.shared.SharedViewModel
import com.bartex.quizday.ui.flags.StatesSealed
import com.bartex.quizday.ui.flags.tabs.flag.FlagsFragment
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class StatesFragment : Fragment(){

    companion object{
        const val TAG = "33333"
    }

    private val statesViewModel by lazy{
        ViewModelProvider(requireActivity()).get(StatesViewModel::class.java)
    }

    private val model: SharedViewModel by activityViewModels()

    private val  mToneGenerator: ToneGenerator by lazy{
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }

    private lateinit var handler : Handler   // Для задержки загрузки следующего флага
    private lateinit var quizLinearLayout  : LinearLayout // root макета фрагмента
    private lateinit var answerTextView : TextView  //для правильно/неправильно
    private lateinit var questionTextView  : TextView  //Для вывода страны-вопроса
    private lateinit var guessButton: ImageView  // текущая кнопка ответа
    private lateinit var progressBarStates: ProgressBar
    private var guessLinearLayouts : Array<LinearLayout?> = arrayOfNulls(3) //кнопки ответов

    private lateinit var chipGroup: ChipGroup
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "StatesFragment onCreateView")
        return inflater.inflate(R.layout.fragment_states, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "StatesFragment onViewCreated")

        navController = Navigation.findNavController(view)

        initHandler()
        initViews(view)
        initChipGroupListener()
        initButtonsListeners()
        initMenu()

        val  isNetworkAvailable = (requireActivity() as MainActivity).getNetworkAvailable()

        //при первом запуске при налиции интернета получаем список стран из сети и заполняем базу данных
        //затем данные получаем из базы
        if (savedInstanceState == null){
            //выделение на Европу при перврй загрузке (можно также запоминать в Pref)
            chipGroup.check(R.id.chip_Europa_states)

            statesViewModel.getDataFromDatabase()
                    .observe(viewLifecycleOwner, {listOfState->
                        if (listOfState.size >200){ //если в базе есть записи
                            renderDataFromDatabase(listOfState)  //берём из базы
                        }else{ //если в базе ничего нет
                            if (isNetworkAvailable){ //если сеть есть
                                //получаем страны из сети и после этого запускаем викторину
                                statesViewModel.getStatesSealed()
                                        .observe(viewLifecycleOwner,  {stateSealed->
                                            renderData(stateSealed)
                                        })
                            }else{//если нет ни сети ни данных в базе - показываем предупреждение
                                showAlertDialog(
                                        getString(R.string.dialog_title_device_is_offline),
                                        getString(R.string.dialog_message_load_impossible)
                                )
                            }
                        }
                    })
        }else{
            val title = model.toolbarTitleFromState.value
            title?.let{model.updateToolbarTitleFromFlag(it)}//обновление тулбара при повороте экрана
        }

        //следим за состоянием конечного автомата
        statesViewModel.getCurrentState()
                .observe(viewLifecycleOwner, { newQuizState ->
                    statesViewModel.saveCurrentState(newQuizState)
                    renderViewState(newQuizState)
                    Log.d(FlagsFragment.TAG, "FlagsFragment onViewCreated: newQuizState = $newQuizState")
                })
    }

    // метод onStart вызывается после onViewCreated.
    override fun onStart() {
        super.onStart()
        Log.d(FlagsFragment.TAG, "FlagsFragment onStart")
        statesViewModel.updateSoundOnOff() //обновляем звук
        statesViewModel.updateNumberFlagsInQuiz() //обновляем число вопросов в викторине
        updateGuessRows(statesViewModel.getGuessRows()) //обновляем число выриантов ответов в викторине
    }

    private fun showAlertDialog(title: String?, message: String?) {
        NoInternetDialogFragment.newInstance(title, message)
                .show(requireActivity().supportFragmentManager, Constants.DIALOG_FRAGMENT)
    }

    private fun initChipGroupListener() {
        chipGroup.setOnCheckedChangeListener { _, id ->
            val newRegion:String = getChipNameById(id)
            if (newRegion != statesViewModel.getCurrentRegion()){
                statesViewModel.saveRegion(newRegion)
                statesViewModel.resetQuiz()

                model.update(newRegion) //обновляем регион и храним в model
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
        getNumberOnChipName(data) //показываем количество стран в регионе
        model.update(data.region)  //обновляем регион  - в случае если без действий уходим на другой фрагмент
        statesViewModel.loadNextFlag(data)
    }

    //следующий вопрос
    private fun showNextFlagState(data: DataFlags) {
        getNumberOnChipName(data)//показываем количество стран в регионе
        model.updateToolbarTitleFromState(getToolbarTitle(data))//обновить номер текущего вопроса
        answerTextView.text = "" //не показывать пока ответ правильный/неправильный
        showNextCountry(data)  //показываем название страны которую нужно угадать*
        showAnswerButtonsNumberAndNames(data)// Добавление кнопок*
        showCorrectAnswerButtom(data) //вешаем правильный ответ на случайную кнопку*
    }

    //неправильный ответ
    private fun showNotWellState(data: DataFlags) {
        getNumberOnChipName(data)//показываем количество стран в регионе
        statesViewModel.writeMistakeInDatabase() //делаем отметку об ошибке в базе данных
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_L, 100) }.start()
        model.updateToolbarTitleFromState(getToolbarTitle(data))//обновить номер текущего вопроса
        showIncorrectAnswer()//показать неправильный ответ
        //todo анимацию встряхивания сделать
        showNextCountry(data) //показываем название страны которую нужно угадать*
        showAnswerButtonsNumberAndNames(data) // Добавление кнопок*
        showCorrectAnswerButtom(data)//вешаем правильный ответ на случайную кнопку*
    }

    // Ответ правильный, но викторина не закончена
    private fun showWellNotLastState(data: DataFlags) {
        getNumberOnChipName(data)//показываем количество стран в регионе
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 50) }.start()
        model.updateToolbarTitleFromState(getToolbarTitle(data))//обновить номер текущего вопроса
        showCorrectAnswer(data) //показать правильный ответ
        disableButtons()  // Блокировка всех кнопок ответов
        handler.postDelayed(
                { //todo сделать анимацию исчезновения флага
                    statesViewModel.loadNextFlag(data)
                }, 1000
        )
    }

    // Ответ правильный и викторина закончена
    private fun showWellAndLastState(data: DataFlags) {
        getNumberOnChipName(data)//показываем количество стран в регионе
        Thread { mToneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 100) }.start()
        model.updateToolbarTitleFromState(getToolbarTitle(data))//обновить номер текущего вопроса
        showCorrectAnswer(data) //показать правильный ответ
        disableButtons() //сделать иконки недоступными
        showNextCountry(data) //показываем название страны которую нужно угадать*

        //если диалог не создан - создаём и передаём данные
        if(statesViewModel.getNeedDialog()){
            val bundle = Bundle()
            bundle. putInt(Constants.TOTAL_QUESTIONS, data.flagsInQuiz )
            bundle. putInt(Constants.TOTAL_GUESSES, data.totalGuesses )
            navController.navigate(R.id.resultDialogState, bundle)
        }
    }
    
    private fun renderDataFromDatabase(data: List<State>?) {
        //сохраняем список стран во ViewModel на время жизни фрагмента
        statesViewModel.saveListOfStates(data as MutableList<State>)
        //переводим конечный автомат в состояние ReadyState
        statesViewModel.resetQuiz()
    }

    private fun renderData(data: StatesSealed?) {
        when(data){
            is StatesSealed.Success -> {
                //показываем макет викторины, скрываем прогресс
                quizLinearLayout.visibility = View.VISIBLE
                progressBarStates.visibility = View.GONE

                //список стран с названиями, столицами, флагами
                val  listStates = data.states as MutableList<State>
                //сохраняем список стран во ViewModel на время жизни фрагмента
                statesViewModel.saveListOfStates(listStates)
                //переводим конечный автомат в состояние ReadyState
                statesViewModel.resetQuiz()
            }
            is StatesSealed.Error ->{
                Toast.makeText(requireActivity(), "${data.error.message}", Toast.LENGTH_SHORT).show()
            }
            is StatesSealed.Loading ->{
                quizLinearLayout.visibility = View.GONE
                progressBarStates.visibility = View.VISIBLE
            }
        }
    }

    private fun initHandler() {
        handler = Handler(requireActivity().mainLooper)
    }

    private fun initViews(view: View) {
        chipGroup =view.findViewById<ChipGroup>(R.id.chip_region_states)

        quizLinearLayout = view.findViewById<View>(R.id.quizLinearLayoutStates) as LinearLayout
        answerTextView = view.findViewById<View>(R.id.answerTextView_states) as TextView
        questionTextView = view.findViewById<View>(R.id.questionTextView_states) as TextView
        progressBarStates = view.findViewById<View>(R.id.progressBarStates) as ProgressBar

        guessLinearLayouts[0] = view.findViewById<View>(R.id.row1LinearLayout_states) as LinearLayout
        guessLinearLayouts[1] = view.findViewById<View>(R.id.row2LinearLayout_states) as LinearLayout
        guessLinearLayouts[2] = view.findViewById<View>(R.id.row3LinearLayout_states) as LinearLayout
    }

    //показываем страну, которую нужно угадать
    private fun showNextCountry(data: DataFlags) {
        data.nextCountry?.nameRus.let { nameRus ->
            questionTextView.text = nameRus
        }
    }

    // Добавление 2, 4, 6 кнопок в зависимости от значения guessRows
    private fun showAnswerButtonsNumberAndNames(data: DataFlags) {
        for (row in 0 until data.guessRows) {
            // Размещение кнопок в currentTableRow
            for (column in 0 until guessLinearLayouts[row]!!.childCount) {
                // Получение ссылки на ImageView ответа
                val answerImageView = guessLinearLayouts[row]!!.getChildAt(column) as ImageView
                // флаг на этой позиции
                val flag = data.listStates[row * 2 + column].flag
                // название страны на этой позиции
                val nameRusState =  data.listStates[row * 2 + column].nameRus
                // тэг- пригодится при щелчке на кнопке
                answerImageView.tag = nameRusState?.let { ButtonTag(row,column, it) }
                //показываем изображение флага
                GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(flag), answerImageView)
                //доступность в зависимости от содержания в списке неправильных ответов
                answerImageView.isEnabled = !data.buttonNotWellAnswerList.contains(nameRusState)
            }
        }
    }

    //вешаем правильный ответ на случайную кнопку
    private fun showCorrectAnswerButtom(data: DataFlags) {
        // Получение строки LinearLayouts
        val randomRow = guessLinearLayouts[data.randomRow]
        // Случайная замена одной кнопки правильным ответом
        val randomImageView = randomRow?.getChildAt(data.randomColumn) as ImageView
        randomImageView.tag = data.correctAnswer?.let {
            ButtonTag(row = data.randomRow, column = data.randomColumn, nameRus = it)}
        val correctFlag = data.nextCountry?.flag
        GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(correctFlag), randomImageView)
    }

    private fun initButtonsListeners() {
        //Перебираем строки в Array<LinearLayout?> - в каждой строке проходим
        // по всем детям LinearLayout, соторых считаем в row.childCount
        //В каждой строке находим кнопку по индексу колонки и устанавливаем слушатель
        for ((index, row) in guessLinearLayouts.withIndex()) {
            for (column in 0 until row!!.childCount) {
                val button = row.getChildAt(column) as ImageView
                button.setOnClickListener(answerButtonListener)
            }
        }
    }

    private val answerButtonListener:   View.OnClickListener =   View.OnClickListener { v ->
        guessButton = v as ImageView //нажатая кнопка ответа
       val tag:ButtonTag =  guessButton.tag as ButtonTag
         //ответ как тэг который определялся как название страны
        statesViewModel.answerImageButtonClick(tag) //определить тип ответа
    }

    private fun getChipNameById(chipId: Int):String {
        return  when (chipId) {
            R.id.chip_all_states -> Constants.REGION_ALL
            R.id.chip_Europa_states -> Constants.REGION_EUROPE
            R.id.chip_Asia_states -> Constants.REGION_ASIA
            R.id.chip_America_states -> Constants.REGION_AMERICAS
            R.id.chip_Oceania_states -> Constants.REGION_OCEANIA
            R.id.chip_Africa_states -> Constants.REGION_AFRICA
            else -> Constants.REGION_EUROPE
        }
    }

    private fun initMenu() {
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    private fun getNumberOnChipName(data: DataFlags) {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            var regionName = ""
            regionName = if (chip.isChecked) {
                statesViewModel.getRegionNameAndNumber(data)
            }else{
                getChipNameById(chip.id)
            }
            chip.text = regionName
        }
    }

    //получаем строку с номером вопроса
    private fun getToolbarTitle(data: DataFlags):String {
        return getString(R.string.question, data.correctAnswers, data.flagsInQuiz)
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

    private fun disableButtons(){
        for (row in guessLinearLayouts){
            for (column in 0 until row!!.childCount ){
                val button = row.getChildAt(column) as ImageView
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