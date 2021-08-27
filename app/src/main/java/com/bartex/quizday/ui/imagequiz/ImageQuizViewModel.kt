package com.bartex.quizday.ui.imagequiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bartex.quizday.ui.adapters.ItemList

class ImageQuizViewModel(application: Application) : AndroidViewModel(application) {

    private val _imageList =  MutableLiveData<List<ItemList>>()
    private val imageRepo: IImageList = ImageListImpl(application)

    fun getImageList():LiveData<List<ItemList>>{
        return _imageList
    }

    fun loadData(){
        _imageList.value = imageRepo.getImageList()
    }
}