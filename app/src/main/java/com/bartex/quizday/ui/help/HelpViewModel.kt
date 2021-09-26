package com.bartex.quizday.ui.help

import androidx.lifecycle.ViewModel
import com.bartex.quizday.App

import com.bartex.quizday.model.repositories.help.HelpRepo
import com.bartex.quizday.model.repositories.help.IHelpRepo

class HelpViewModel(
        private val helpRepo: IHelpRepo = HelpRepo(app = App.instance)
): ViewModel() {

    fun getHelpText(): String {
        return  helpRepo.getHelpText()
    }
}