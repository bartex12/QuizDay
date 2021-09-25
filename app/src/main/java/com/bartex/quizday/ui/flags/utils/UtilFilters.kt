package com.bartex.quizday.ui.flags.utils

import com.bartex.quizday.model.entity.State

object UtilFilters {

    fun filterData(st: State) :Boolean{
      return  st.name!=null && st.capital!=null  && st.name.isNotBlank() &&
                st.capital.isNotBlank() && st.flags?.get(0).toString().isNotBlank()
                && st.name != "Puerto Rico" && st.name !=  "French Guiana" &&
                st.flags?.get(0)!!.startsWith("https://restcountries.com")
    }
}