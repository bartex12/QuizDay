package com.bartex.quizday.ui.flags

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.App
import com.bartex.quizday.model.TestFlagClass
import com.bartex.quizday.model.api.IDataSourceState
import com.bartex.quizday.model.repositories.IStatesRepo
import com.bartex.quizday.model.repositories.StatesRepo
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class FlagsViewModel(
    var statesRepo: IStatesRepo = StatesRepo(
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation()
                .create()
            ))
            .build()
            .create(IDataSourceState::class.java)
    )
) : ViewModel()  {

    companion object{
        const val TAG = "33333"
        const val baseUrl =  "https://restcountries.eu/rest/v2/"
    }

    val repo:IRepo = RepoImpl(App.instance)

  fun   getFlags(): MutableList<TestFlagClass>{
      return repo.getFlags()
  }
   //_______________________________________________________________

    private val listStates = MutableLiveData<StatesSealed>()

    fun getStatesSealed() : LiveData<StatesSealed> {
        return listStates
    }

    fun loadDataSealed(){
        //начинаем загрузку данных
        listStates.value = StatesSealed.Loading(null)

        statesRepo.getStates()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({states->
                // если данные загружены - выставляем value в MutableLiveData
                listStates.value = StatesSealed.Success(state = states)
            },{error ->
                //если произошла ошибка - выставляем value в MutableLiveData в ошибку
                listStates.value = StatesSealed.Error(error = error)
            })
    }
}