package com.bartex.quizday.model.repositories.state.roomcash

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.bartex.quizday.model.common.MapOfCapital
import com.bartex.quizday.model.common.MapOfRegion
import com.bartex.quizday.model.common.MapOfState
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.room.Database
import com.bartex.quizday.room.tables.RoomState
import com.google.gson.annotations.Expose
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

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
    override fun getStatesFromDatabase(): Single<MutableList<State>> =
        Single.fromCallable {
            db.stateDao.getAll().map{
                State(it.capital,it.flag, it.name, it.region,
                        it.nameRus, it.capitalRus, it.regionRus
                )
            }.toMutableList()
    } .subscribeOn(Schedulers.io())

    //получение списка стран с учётом региона (кроме отфильтрованных из-за неполных данных)
    override fun getRegionStatesFromCash(region: String): Single<List<State>>  =
        Single.fromCallable {
            db.stateDao.getRegionStates(region).map{
                State(it.capital,it.flag, it.name, it.region,
                        it.nameRus, it.capitalRus, it.regionRus )
            }
        }
            .subscribeOn(Schedulers.io())

    //сделать отметку об ошибке
    override fun writeMistakeInDatabase(mistakeAnswer: String): Single<Boolean> =
        Single.fromCallable {
            val mistakeRoomState: RoomState = db.stateDao.getStateByNameRus(mistakeAnswer) //получаем страну по имени
            if (mistakeRoomState.mistake == 0) { //если статус ошибки = 0
                mistakeRoomState.mistake = 1 //меняем статус ошибки на 1
               db.stateDao.update(mistakeRoomState) //обновляем запись в базе
            }
            //проверяем как прошла запись
            val result:Int =  db.stateDao.getMistakeByNameRus(mistakeAnswer)
            result == 1 //если 1 - возвращаем true, иначе false
            }
        .subscribeOn(Schedulers.io())

    //удалить отметку об ошибке
    override fun removeMistakeFromDatabase(nameRus: String): Single<Boolean> =
        Single.fromCallable {
            val mistakeRoomState: RoomState = db.stateDao.getStateByNameRus(nameRus) //получаем страну по имени
            if (mistakeRoomState.mistake == 1) { //если статус ошибки = 0
                mistakeRoomState.mistake = 0 //меняем статус ошибки на 1
                db.stateDao.update(mistakeRoomState) //обновляем запись в базе
            }
            //проверяем как прошло удаление отметки
            val result:Int =  db.stateDao.getMistakeByNameRus(nameRus)
            result == 0 //если 0 - возвращаем true, иначе false
        }
            .subscribeOn(Schedulers.io())

    //получить список стран на которых сделаны ошибки
    override fun getMistakesFromDatabase(): Single<List<State>> =
            Single.fromCallable {
              val listOfMistakes:List<RoomState> =  db.stateDao.getMistakesList()
               val states =  listOfMistakes.map{
                    State(it.capital, it.flag, it.name, it.region, it.nameRus, it.capitalRus, it.regionRus
                    )
                }
                states
            }
                    .subscribeOn(Schedulers.io())

    override fun loadAllData(): Single<MutableList<State>> =
         Single.fromCallable {
            db.stateDao.getAll().map{
                State(it.capital, it.flag, it.name, it.region, it.nameRus, it.capitalRus, it.regionRus)
            }.toMutableList()
         }.subscribeOn(Schedulers.io())

    override fun getAllMistakesLive(): LiveData<List<RoomState>> {
        return db.stateDao.getAllMistakesLive()
    }


}


