package com.bartex.quizday.model.fsm

import com.bartex.quizday.model.fsm.model.DataFlags

sealed class Action{
    class OnStartQuiz(val data: DataFlags) : Action()
    class OnWellNotLastClicked(val data: DataFlags): Action()
    class OnWellAndLastClicked(val data: DataFlags): Action()
    class OnNotWellClicked(val data: DataFlags): Action()
    class OnResetQuiz() : Action()
}
