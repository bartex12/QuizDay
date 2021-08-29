package com.bartex.quizday.ui.flags

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants

class ResultDialog: DialogFragment() {

    private var total:Int = 0
    private var totalGuesses:Int = 0

    interface OnResultListener{
        fun resetQuiz()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?. let{
            total = it.getInt(Constants.TOTAL_QUESTIONS)
            totalGuesses = it.getInt(Constants.TOTAL_GUESSES)

        }
    }

    @SuppressLint("StringFormatMatches")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity )
        builder.setMessage(
                getString(R.string.results, total, totalGuesses, total*100/totalGuesses.toDouble())
        )
        builder.setPositiveButton(R.string.reset_quiz) { _, _ ->
            val navController = Navigation.findNavController(requireParentFragment().requireView())
            //navController.previousBackStackEntry?.savedStateHandle?.set(Constants.RESET_KEY, true)
            navController.navigate(R.id.action_resultDialog_to_flagsFragment)
            //dismiss()
        }
        return builder.create().apply {
            setCanceledOnTouchOutside(false)
        }
    }
}