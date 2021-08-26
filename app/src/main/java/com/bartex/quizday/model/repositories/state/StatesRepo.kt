package com.bartex.quizday.model.repositories.state

import com.bartex.quizday.model.api.IDataSourceState
import com.bartex.quizday.model.common.MapOfCapital
import com.bartex.quizday.model.common.MapOfRegion
import com.bartex.quizday.model.common.MapOfState
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.model.repositories.state.roomcash.IRoomStateCash
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class StatesRepo(private val api: IDataSourceState, private val roomCash: IRoomStateCash): IStatesRepo {

    override fun getStates(isNetworkAvailable:Boolean): Single<List<State>> =
        if(isNetworkAvailable){
            api.getStates()
                .flatMap {states->
                    //отбираем только те, где значения не равны null
                    val statesFiltered = states.filter { st->
                        st.name!=null && st.capital!=null && st.flag!=null &&
                                st.name.isNotBlank() && st.capital.isNotBlank() && st.flag.isNotBlank()
                    }
                    //добавляем русские названия из Map
                    states.map { state ->
                        state.nameRus = MapOfState .mapStates[state.name]?:"Unknown"
                        state.capitalRus = MapOfCapital.mapCapital[state.capital] ?:"Unknown"
                        state.regionRus = MapOfRegion.mapRegion[state.region] ?:"Unknown"
                    }
                    roomCash.doStatesCash(statesFiltered) //пишем в базу и возвращаем Single<List<State>>
                }
        }else{
           roomCash.getStatesFromCash() //получение списка стран  из кэша
        }
           .subscribeOn(Schedulers.io())
}