package com.bartex.quizday.ui.textquiz

import com.bartex.quizday.model.entity.TextEntity

sealed class GuessState {
    data class Success(val states: TextEntity) : GuessState()
    data class Error(val error: Throwable) : GuessState()
    data class Loading(val progress: Int) : GuessState()
}