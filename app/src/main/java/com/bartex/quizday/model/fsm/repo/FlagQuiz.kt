package com.bartex.quizday.model.fsm.repo

import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.fsm.entity.Answer
import com.bartex.quizday.model.fsm.entity.DataFlags
import java.security.SecureRandom

class FlagQuiz:IFlagQuiz {

    //здесь сбрасываем переменные и очищаем списки а также формируем список с необходимым
    // числом флагов для новой викторины
    override fun resetQuiz(listStates: MutableList<State>, dataFlags:DataFlags, region:String):DataFlags {
        //фильтруем список по региону
        if (region == Constants.REGION_ALL){
            dataFlags.listStates = listStates
        }else{
            val filteredList =   listStates.filter { state->
                state.regionRus == region
            } as MutableList<State>
            dataFlags.listStates = filteredList //передаём список в класс данных
        }
        //сбрасываем все остальные данные
        dataFlags.correctAnswer = null //правильный ответ
        dataFlags.typeAnswer = null //тип ответа
        dataFlags.nextCountry = null //следующая страна для угадывания флага
        dataFlags.row = 0  //номер строки кнопки ответа
        dataFlags.column = 0 //номер столбца кнопки ответа
        dataFlags.correctAnswers = 0  // Сброс количества правильных ответов
        dataFlags.totalGuesses = 0 //  Сброс общего количества попыток
        dataFlags.quizCountriesList.clear()  // Очистка списка стран текущей викторины
        dataFlags.buttonNotWellAnswerList.clear() //очистка списка кнопок с неправильными ответами

        var flagCounter = 1
        //dataFlags.listStates - список с названиями стран, столицами, флагами
        val numberOfFlags = dataFlags.listStates.size
        // Добавление FLAGS_IN_QUIZ штук  случайных стран в quizCountriesList
        while (flagCounter <= dataFlags.flagsInQuiz) {
            val randomIndex = SecureRandom().nextInt(numberOfFlags)
            // Получение случайного элемента списка - экземпляра класса State
            val state:State = dataFlags.listStates[randomIndex]
            // Если элемент списка еще не был выбран, добавляем его в список  для текущей викторины
            if (!dataFlags.quizCountriesList.contains(state)) {
                dataFlags.quizCountriesList.add(state)  // Добавить элемент в список для викторины
                ++flagCounter
            }
        }
        return dataFlags
    }

    //загрузка новых данных для викторины
    override fun loadNextFlag(dataFlags:DataFlags):DataFlags {
        //очистка списка кнопок с неправильными ответами
        dataFlags.buttonNotWellAnswerList.clear()
        // Получение  следующей страны для угадывания флага
        dataFlags.nextCountry = dataFlags.quizCountriesList.removeAt(0)
        // Обновление правильного ответа
        dataFlags.correctAnswer = dataFlags.nextCountry?.nameRus
        dataFlags.listStates.shuffle()  // Перестановка стран в списке

        var correctIndex = 0
        for (i in 0 until dataFlags.listStates.size ){
            if(dataFlags.listStates[i].nameRus == dataFlags.correctAnswer){
                correctIndex = i //получение индекса правильного ответа
            }
        }
        // Помещение правильного ответа в конец listStates
        dataFlags.listStates.add(dataFlags.listStates.removeAt(correctIndex))
        ++dataFlags.correctAnswers //увеличиваем номер ответа
        return dataFlags
    }

    override fun getTypeAnswer(guess:String, dataFlags:DataFlags) :DataFlags {
        ++dataFlags.totalGuesses  // Увеличение количества попыток пользователя
        // Если ответ правилен
        if (guess == dataFlags.correctAnswer) {
            //если ответ правильный и последний
            if (dataFlags.correctAnswers == dataFlags.flagsInQuiz) {
                dataFlags.typeAnswer =Answer.WellAndLast
            }else {
                // Ответ правильный, но викторина не закончена
                dataFlags.typeAnswer = Answer.WellNotLast
            }
        }else {
            // Неправильный ответ
            dataFlags.typeAnswer =  Answer.NotWell
            //добавляем в список неправильных ответов чтобы задавать свойство isEnable кнопок
            dataFlags.buttonNotWellAnswerList.add(guess)
        }
        return dataFlags
    }
}