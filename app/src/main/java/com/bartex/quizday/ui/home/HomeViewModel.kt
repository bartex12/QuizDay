package com.bartex.quizday.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bartex.quizday.ui.adapters.ItemList

class HomeViewModel(application: Application) :AndroidViewModel(application) {

    private val _mainList =  MutableLiveData<MutableList<ItemList>>()
    private val mainRepo:IMainList = MainListImpl(application)

    fun getMainList():LiveData<MutableList<ItemList>>{
        return _mainList
    }

    fun loadData(){
        _mainList.value = mainRepo.getList()
    }
}