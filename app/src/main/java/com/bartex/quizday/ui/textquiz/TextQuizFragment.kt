package com.bartex.quizday.ui.textquiz

import android.os.Bundle
import android.os.Handler
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
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.entity.TextEntity
import com.bartex.quizday.network.NoInternetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_textquiz.*

class TextQuizFragment : Fragment() {

    private val textQuizViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TextQuizViewModel::class.java)
    }

    private lateinit var handler: Handler   // Для задержки загрузки
    private lateinit var questionTextView: TextView
    private lateinit var answerInputLayout: TextInputLayout
    private lateinit var buttonSend: MaterialButton

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
    }

    private fun initButtonListener() {
        buttonSend.setOnClickListener {
            val guess: String = answerInputLayout.editText?.text.toString()
            if (guess.isNotEmpty())
                text_stub.text = guess //удалить когда появится ViewModel
            //textQuizViewModel.answer(guess)
        }
    }

    private fun renderData(data: GuessState?) {
        when (data) {
            is GuessState.Success -> {
                val states = data.states
                text_stub.text = states.answer
                answerInputLayout.visibility = View.VISIBLE
                buttonSend.visibility = View.VISIBLE
            }
            is GuessState.Error -> {
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