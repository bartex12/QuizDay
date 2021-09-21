package com.bartex.quizday.ui.flags.base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants

abstract class BaseResultDialog: DialogFragment(){
     private var total:Int = 0
     var totalGuesses:Int = 0

    abstract fun getCurrentViewModel(): BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?. let{
            total = it.getInt(Constants.TOTAL_QUESTIONS)
            totalGuesses = it.getInt(Constants.TOTAL_GUESSES)
        }
        //предотвращаем повторное создание диалога при повороте экрана
        getCurrentViewModel().setNeedDialog(false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_statistica, null)
        val builder = AlertDialog.Builder(activity )
        dialog?.requestWindowFeature(STYLE_NO_TITLE);
        isCancelable = false
        builder.setView(view)

        view.findViewById<TextView>(R.id.results).text = getString(R.string.result)
            view.findViewById<TextView>(R.id.questions).text = getString(R.string.results_questions, total)
        view.findViewById<TextView>(R.id.attempts).text = getString(R.string.results_attempts, totalGuesses)
        view.findViewById<TextView>(R.id.performance).text =
            getString(R.string.results_performance, total*100/totalGuesses.toDouble())

        view.findViewById<TextView>(R.id.button_ok).setOnClickListener {
            getCurrentViewModel().resetQuiz() //просто вызываем метод вьюмодели
            dismiss()
        }
        return builder.create().apply {
            setCanceledOnTouchOutside(false)
        }
    }
    
}
