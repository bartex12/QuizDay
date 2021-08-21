package com.bartex.quizday.model.fsm.model

sealed class Answer{
   object NotWell:Answer()
   object WellNotLast:Answer()
   object WellAndLast:Answer()
}
