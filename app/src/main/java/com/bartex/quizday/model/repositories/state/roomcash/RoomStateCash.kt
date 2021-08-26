package com.bartex.quizday.model.repositories.state.roomcash

import com.bartex.quizday.model.common.MapOfCapital
import com.bartex.quizday.model.common.MapOfRegion
import com.bartex.quizday.model.common.MapOfState
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.room.Database
import com.bartex.quizday.room.tables.RoomState
import io.reactivex.rxjava3.core.Single

class RoomStateCash(val db:Database):IRoomStateCash {

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

    override fun getStatesFromCash(): Single<List<State>> {
        return Single.fromCallable {
            db.stateDao.getAll().map{
                State(it.capital,it.flag, it.name, it.region,
                    it.nameRus, it.capitalRus, it.regionRus
                )
            }
        }
    }
}