package com.bartex.quizday.model.fsm.substates

import com.bartex.quizday.model.fsm.Action
import com.bartex.quizday.model.fsm.model.DataFlags
import com.bartex.quizday.model.fsm.IFlagState

class ReadyState(val data: DataFlags): IFlagState {
    override fun consumAction(action: Action): IFlagState {
        return when(action){
            is Action.OnNotWellClicked -> NotWellState(data)
            is Action.OnWellNotLastClicked -> WellNotLastState(data)
            is Action.OnWellAndLastClicked -> WellAndLastState(data)
            else -> throw IllegalStateException("Invalid action $action passed to state $this")
        }
    }
}