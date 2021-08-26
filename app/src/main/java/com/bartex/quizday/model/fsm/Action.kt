package com.bartex.quizday.model.fsm

import com.bartex.quizday.model.fsm.entity.DataFlags

sealed class Action{
    class OnNextFlagClicked(val data: DataFlags) : Action()
    class OnWellNotLastClicked(val data: DataFlags): Action()
    class OnWellAndLastClicked(val data: DataFlags): Action()
    class OnNotWellClicked(val data: DataFlags): Action()
    object OnResetQuiz : Action()
}
