package com.bartex.quizday.ui.imagequiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.model.MainList
import com.bartex.quizday.ui.home.IMainList
import com.bartex.quizday.ui.home.MainListImpl

class ImageQuizViewModel(application: Application) : AndroidViewModel(application) {

    private val _imageList =  MutableLiveData<List<MainList>>()
    val imageRepo: IImageList = ImageListImpl(application)

    fun getImageList():LiveData<List<MainList>>{
        return _imageList
    }

    fun loadData(){
        _imageList.value = imageRepo.getImageList()
    }
}