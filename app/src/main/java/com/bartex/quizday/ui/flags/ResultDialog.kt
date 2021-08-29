package com.bartex.quizday.ui.flags

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants

class ResultDialog: DialogFragment() {

    private var total:Int = 0
    private var totalGuesses:Int = 0
    private var onResultListener:OnResultListener? = null

    companion object{
        fun  newInstance( total: Int,totalGuesses :Int):ResultDialog{
            val frag = ResultDialog()
            val bundle = Bundle()
            bundle. putInt(Constants.TOTAL_QUESTIONS,total )
            bundle. putInt(Constants.TOTAL_GUESSES, totalGuesses )
            frag.arguments = bundle
            return frag
        }
    }

    interface OnResultListener{
        fun resetQuiz()
    }

    @JvmName("setOnResultListener1")
    fun setOnResultListener(onResultListener:OnResultListener){
        this.onResultListener = onResultListener
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

        builder.setPositiveButton(R.string.reset_quiz) { dialog, id ->
            onResultListener?.resetQuiz()
        }

        return builder.create().apply {
            setCanceledOnTouchOutside(false)
        }
    }
}