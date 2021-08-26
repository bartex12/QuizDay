package com.bartex.quizday.model.fsm.substates

import com.bartex.quizday.model.fsm.Action
import com.bartex.quizday.model.fsm.entity.DataFlags
import com.bartex.quizday.model.fsm.IFlagState

class ReadyState(val data: DataFlags): IFlagState {
    override fun executeAction(action: Action): IFlagState {
        return when(action){
            is Action.OnNextFlagClicked -> NextFlagState(data)
            else -> throw IllegalStateException("Invalid action $action passed to state $this")
        }
    }
}