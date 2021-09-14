package com.bartex.quizday.model.fsm.entity

import com.bartex.quizday.model.entity.TextEntity
import java.io.Serializable

data class DataText(
    var answer: String? = null,
    var question: String? = null,
    var category: String? = null,
    var listText: MutableList<TextEntity> = mutableListOf()
) : Serializable