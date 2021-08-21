package com.bartex.quizday.model.fsm.substates

import com.bartex.quizday.model.fsm.Action
import com.bartex.quizday.model.fsm.IFlagState
import com.bartex.quizday.model.fsm.model.DataFlags

class WellAndLastState(val data: DataFlags):IFlagState {
    override fun consumAction(action: Action): IFlagState {
        return when(action){
            is Action.OnResetQuiz -> ReadyState(DataFlags())
            else -> throw IllegalStateException("Invalid action $action passed to state $this")
        }
    }
}