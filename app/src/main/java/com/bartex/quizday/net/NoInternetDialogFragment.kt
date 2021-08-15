package com.bartex.quizday.net

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.bartex.quizday.R

class NoInternetDialogFragment : AppCompatDialogFragment() {

    companion object {
        private const val TITLE_EXTRA = "TITLE_EXTRA"
        private const val MESSAGE_EXTRA = "MESSAGE_EXTRA"

        fun newInstance(title: String?, message: String?): NoInternetDialogFragment {
            val dialogFragment = NoInternetDialogFragment()
            val args = Bundle()
            args.putString(TITLE_EXTRA, title)
            args.putString(MESSAGE_EXTRA, message)
            dialogFragment.arguments = args
            return dialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var title:String? = ""
        var message:String? = ""
        val args = arguments
        if (args != null) {
            title = args.getString(TITLE_EXTRA)
            message = args.getString(MESSAGE_EXTRA)
        }
        val builder = AlertDialog.Builder(requireContext())
        var finalTitle: String? = context?.getString(R.string.dialog_title_stub)
        if (!title.isNullOrBlank()) {
            finalTitle = title
        }
        builder.setTitle(finalTitle)
        if (!message.isNullOrBlank()) {
            builder.setMessage(message)
        }
        builder.setCancelable(true)
        builder.setPositiveButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
        return builder.create()
    }
}
