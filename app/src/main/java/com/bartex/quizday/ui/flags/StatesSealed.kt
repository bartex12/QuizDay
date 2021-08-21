package com.bartex.quizday.ui.flags

import com.bartex.quizday.model.entity.State

sealed class StatesSealed{
    data class Success(val state:List<State>):StatesSealed()
    data class Error(val error:Throwable):StatesSealed()
    data class Loading(val progress:Int):StatesSealed()
}
