package com.bartex.quizday.ui.flags

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bartex.quizday.R

class ResultDialog(private val total: Int,
                   private val totalGuesses :Int,
                   private val onResultListener:OnResultListener): DialogFragment() {

    interface OnResultListener{
        fun resetQuiz()
    }

    @SuppressLint("StringFormatMatches")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity )
        builder.setMessage(
                getString(R.string.results, total, totalGuesses, total*100/totalGuesses.toDouble())
        )

        builder.setPositiveButton(R.string.reset_quiz) { dialog, id ->
            onResultListener.resetQuiz()
        }

        return builder.create().apply {
            setCanceledOnTouchOutside(false)
        }
    }
}