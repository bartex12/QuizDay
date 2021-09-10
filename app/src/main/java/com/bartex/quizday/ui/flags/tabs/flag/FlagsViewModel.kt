package com.bartex.quizday.ui.flags.tabs.flag

import com.bartex.quizday.model.fsm.Action
import com.bartex.quizday.model.fsm.entity.Answer
import com.bartex.quizday.ui.flags.base.BaseViewModel

class FlagsViewModel : BaseViewModel()  {

    //по типу ответа при щелчке по кнопке задаём состояние
    fun answer(guess:String){
        dataFlags = storage.getTypeAnswer(guess, dataFlags)
        when(dataFlags.typeAnswer){
            Answer.NotWell -> {
                currentQuizState.value = currentState.executeAction(Action.OnNotWellClicked(dataFlags))
            }
            Answer.WellNotLast -> {
                currentQuizState.value =  currentState.executeAction(Action.OnWellNotLastClicked(dataFlags))
            }
            Answer.WellAndLast -> {
                currentQuizState.value = currentState.executeAction(Action.OnWellAndLastClicked(dataFlags))
            }
        }
    }

}