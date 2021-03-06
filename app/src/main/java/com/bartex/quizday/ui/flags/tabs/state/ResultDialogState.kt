package com.bartex.quizday.ui.flags.tabs.state

import androidx.lifecycle.ViewModelProvider
import com.bartex.quizday.ui.flags.base.BaseResultDialog
import com.bartex.quizday.ui.flags.base.BaseViewModel

class ResultDialogState: BaseResultDialog() {
    override fun getCurrentViewModel(): BaseViewModel {
      return ViewModelProvider(requireActivity()).get(StatesViewModel::class.java)
    }
}