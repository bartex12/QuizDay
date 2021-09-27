package com.bartex.quizday.ui.textquiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bartex.quizday.model.api.IDataSourceText
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.TextEntity
import com.bartex.quizday.model.repositories.guess.GuessRepo
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TextQuizViewModel(
    private var guessRepo: GuessRepo = GuessRepo(
        Retrofit.Builder()
            .baseUrl(Constants.baseUrlText)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .excludeFieldsWithoutExposeAnnotation()
                        .create()
                )
            ).build().create(IDataSourceText::class.java)
    )
) : ViewModel() {

    private var dataGuess: TextEntity? = null
    private val guessState = MutableLiveData<GuessState>()

    fun getStateGuess(): LiveData<GuessState> {
        loadData()
        return guessState
    }

    fun loadData() {
        guessRepo.getGuess()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                guessState.value = GuessState.Success(states = result)
                dataGuess = result.get(0)
            }, {
                guessState.value = GuessState.Error(error = it)
            })
    }
}