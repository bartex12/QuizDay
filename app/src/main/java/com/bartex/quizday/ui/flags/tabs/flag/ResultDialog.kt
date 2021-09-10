package com.bartex.quizday.ui.flags.tabs.flag

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants

class ResultDialog: DialogFragment() {

    private var total:Int = 0
    private var totalGuesses:Int = 0

    private val flagsViewModel by lazy{
        ViewModelProvider(requireActivity()).get(FlagsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?. let{
            total = it.getInt(Constants.TOTAL_QUESTIONS)
            totalGuesses = it.getInt(Constants.TOTAL_GUESSES)
        }
        //предотвращаем повторное создание диалога при повороте экрана
        flagsViewModel.setNeedDialog(false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity )
        builder.setMessage(
                getString(R.string.results, total, totalGuesses, total*100/totalGuesses.toDouble())
        )
        builder.setPositiveButton(R.string.reset_quiz) { _, _ ->
            flagsViewModel.resetQuiz() //просто вызываем метод вьюмодели
        }
        return builder.create().apply {
            setCanceledOnTouchOutside(false)
        }
    }
}