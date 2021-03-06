package com.bartex.quizday.model.repositories.state

import com.bartex.quizday.model.api.IDataSource
import com.bartex.quizday.model.common.MapOfCapital
import com.bartex.quizday.model.common.MapOfRegion
import com.bartex.quizday.model.common.MapOfState
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import com.bartex.quizday.ui.flags.utils.UtilFilters
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class StatesRepo(private val dataSource: IDataSource, private val roomCash: IRoomStateCash): IStatesRepo {

    override fun getStates(): Single<List<State>> =
            dataSource.getStates()
                .flatMap {states->
                    //отбираем только те, где полные данные
                    val statesFiltered = states.filter { st->
                        UtilFilters.filterData(st)
                    }
                    //добавляем русские названия из Map
                    states.map { state ->
                        state.nameRus = MapOfState .mapStates[state.name]?:"Unknown"
                        state.capitalRus = MapOfCapital.mapCapital[state.capital] ?:"Unknown"
                        state.regionRus = MapOfRegion.mapRegion[state.continent] ?:"Unknown"
                    }
                    roomCash.doStatesCash(statesFiltered) //пишем в базу и возвращаем Single<List<State>>
                }
                    .subscribeOn(Schedulers.io())

    //получение списка стран из базы данных с учётом региона
    override fun getStatesFromCash(region: String):  Single<List<State>> =
      roomCash.getRegionStatesFromCash(region)
              .subscribeOn(Schedulers.io())

}