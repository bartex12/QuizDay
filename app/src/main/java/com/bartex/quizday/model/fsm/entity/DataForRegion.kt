package com.bartex.quizday.model.fsm.entity

import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State

data class DataForRegion(
        var listOfStatesFromNet: MutableList<State> = mutableListOf(), //список стран из сети
        var currentRegion:String = Constants.REGION_EUROPE, //текущий регион
)
