package com.bartex.quizday.model.fsm

import java.io.Serializable

interface IFlagState:Serializable {
    fun executeAction(action:Action):IFlagState
}