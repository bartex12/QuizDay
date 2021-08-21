package com.bartex.quizday.model.fsm

import java.io.Serializable

interface IFlagState:Serializable {
    fun consumAction(action:Action):IFlagState
}