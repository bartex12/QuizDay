package com.bartex.quizday.ui.textquiz

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bartex.quizday.MainActivity
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.TextEntity
import com.bartex.quizday.network.NoInternetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_textquiz.*

class TextQuizFragment : Fragment() {

    private val textQuizViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TextQuizViewModel::class.java)
    }

    private var states: TextEntity? = null
    private lateinit var handler: Handler   // Для задержки загрузки
    private lateinit var questionTextView: TextView
    private lateinit var answerInputLayout: TextInputLayout
    private lateinit var buttonSend: MaterialButton
    private lateinit var answer: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_textquiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHandler()
        initViews(view)
        initButtonListener()

        val isNetworkAvailable = (requireActivity() as MainActivity).getNetworkAvailable()

        if (savedInstanceState == null) {
            if (isNetworkAvailable) { //если сеть есть
                textQuizViewModel.getStateGuess()
                    .observe(viewLifecycleOwner, {
                        renderData(it)
                    })
            } else {
                showAlertDialog(
                    getString(R.string.dialog_title_device_is_offline),
                    getString(R.string.dialog_message_load_impossible)
                )
            }
        }
    }

    private fun initHandler() {
        handler = Handler(requireActivity().mainLooper)
    }

    private fun initViews(view: View) {
        questionTextView = view.findViewById<View>(R.id.text_stub) as TextView
        answerInputLayout = view.findViewById<View>(R.id.input_answer) as TextInputLayout
        buttonSend = view.findViewById<View>(R.id.button_send) as MaterialButton
        answer = view.findViewById(R.id.answer) as TextView

    }

    private fun initButtonListener() {
        buttonSend.setOnClickListener {
            val answer: String = answerInputLayout.editText?.text.toString()
            if (answer.isNotEmpty()) {
                Log.d("exp", states?.answer.toString())
                Log.d("act", answer)
                if (states?.answer.equals(answer))
                    correctAnswer()
                else notCorrectAnswer()
                handler.postDelayed(
                    { //todo сделать анимацию исчезновения флага
                        textQuizViewModel.loadData()
                    }, 1000
                )
            }
        }
    }

    private fun correctAnswer() {
        answer.text = "Правильно!"
        answer.visibility = View.VISIBLE
        buttonSend.visibility = View.GONE
        answerInputLayout.visibility = View.GONE
    }

    private fun notCorrectAnswer() {
        answer.text = "Неправильно!"
        answer.visibility = View.VISIBLE
        buttonSend.visibility = View.GONE
        answerInputLayout.visibility = View.GONE
    }

    private fun renderData(data: GuessState?) {
        when (data) {
            is GuessState.Success -> {
                val states = data.states
                states.get(0).answer?.let { Log.d("Answer", it) }
                text_stub.text = states.get(0).question
                answerInputLayout.visibility = View.VISIBLE
                buttonSend.visibility = View.VISIBLE
                answer.visibility = View.GONE
                answerInputLayout.editText?.text = null
            }
            is GuessState.Error -> {
                Log.d("DEBAG", "${data.error.message}")
                Toast.makeText(requireActivity(), "${data.error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
            is GuessState.Loading -> {
                answerInputLayout.visibility = View.GONE
                buttonSend.visibility = View.GONE
            }
        }
    }

    private fun showAlertDialog(title: String?, message: String?) {
        NoInternetDialogFragment.newInstance(title, message)
            .show(requireActivity().supportFragmentManager, Constants.DIALOG_FRAGMENT)
    }
}