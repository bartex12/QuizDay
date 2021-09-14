package com.bartex.quizday.ui.flags.tabs.flag

import androidx.lifecycle.ViewModelProvider
import com.bartex.quizday.ui.flags.base.BaseResultDialog
import com.bartex.quizday.ui.flags.base.BaseViewModel

class ResultDialogFlags: BaseResultDialog() {

    override fun getCurrentViewModel(): BaseViewModel {
      return  ViewModelProvider(requireActivity()).get(FlagsViewModel::class.java)
    }

}