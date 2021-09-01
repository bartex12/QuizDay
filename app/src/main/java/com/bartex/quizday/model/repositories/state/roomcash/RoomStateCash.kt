package com.bartex.quizday.model.repositories.state.roomcash

import androidx.room.Query
import com.bartex.quizday.model.common.MapOfCapital
import com.bartex.quizday.model.common.MapOfRegion
import com.bartex.quizday.model.common.MapOfState
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.room.Database
import com.bartex.quizday.room.tables.RoomState
import io.reactivex.rxjava3.core.Single

class RoomStateCash(val db:Database):IRoomStateCash {

    //запись в базу данных и возвращение исходного списка
    override fun doStatesCash(listStates: List<State>): Single<List<State>> {
       return Single.fromCallable {
           val roomState  = listStates.map {
               RoomState(
                   it.capital ?: "",
                   it.flag ?: "",
                   it.name ?: "",
                   it.region ?: "",
                   MapOfState.mapStates[it.name] ?:"Unknown",
                   MapOfCapital.mapCapital[it.capital] ?:"Unknown",
                   MapOfRegion.mapRegion[it.region] ?:"Unknown"
               )
           }
           db.stateDao.insert(roomState)
           listStates
       }
    }

    //получение списка всех стран (кроме отфильтрованных из-за неполных данных)
    override fun getStatesFromCash(): Single<List<State>> {
        return Single.fromCallable {
            db.stateDao.getAll().map{
                State(it.capital,it.flag, it.name, it.region,
                    it.nameRus, it.capitalRus, it.regionRus
                )
            }
        }
    }

    //получение списка стран с учётом региона (кроме отфильтрованных из-за неполных данных)
    override fun getRegionStatesFromCash(region: String): Single<List<State>>  {
        return Single.fromCallable {
            db.stateDao.getRegionStates(region).map{
                State(it.capital,it.flag, it.name, it.region,
                        it.nameRus, it.capitalRus, it.regionRus )
            }
        }
    }

    //сделать отметку об ошибке
    override fun writeMistakeInDatabase(notWellAnswer: String) {
        val mistakeState = db.stateDao.getFlagByStateRus(notWellAnswer) //получаем страну по имени
        if (mistakeState.mistake ==0){ //если статус ошибки = 0
            mistakeState.mistake = 1 //меняем статус ошибки на 1
            db.stateDao.update(mistakeState) //обновляем запись в базе
        }
    }

}
