package com.bartex.quizday.model.fsm.entity

sealed class Answer{
   object NotWell:Answer()
   object WellNotLast:Answer()
   object WellAndLast:Answer()
}
