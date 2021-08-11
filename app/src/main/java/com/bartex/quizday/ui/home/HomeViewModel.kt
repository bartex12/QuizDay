package com.bartex.quizday.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.model.MainList

class HomeViewModel(application: Application) :AndroidViewModel(application) {

    private val _mainList =  MutableLiveData<List<MainList>>()
    val mainRepo:IMainList = MainListImpl(application)

    fun getMainList():LiveData<List<MainList>>{
        //loadData()
        return _mainList
    }

    fun loadData(){
        _mainList.value = mainRepo.getList()
    }
}