package com.bartex.quizday.model.repositories

import com.bartex.quizday.model.api.IDataSourceState
import com.bartex.quizday.model.common.MapOfCapital
import com.bartex.quizday.model.common.MapOfRegion
import com.bartex.quizday.model.common.MapOfState
import com.bartex.quizday.model.entity.State
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class StatesRepo(val api: IDataSourceState): IStatesRepo{

    override fun getStates(): Single<List<State>> =
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
               Single.fromCallable {statesFiltered}
           }
           .subscribeOn(Schedulers.io())
}