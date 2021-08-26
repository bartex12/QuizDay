package com.bartex.quizday.model.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

//Аннотация @Parcelize говорит о необходимости сгенерировать весь boilerplate-код,
// необходимый для работы Parcelable, автоматически, избавляя нас от рутины его написания вручную.
//Для того, чтобы работала аннотация @Parcelize нужно добавить plugin с id 'kotlin-android-extensions'
@Parcelize
data class State(
    @Expose val capital :String? = null,
    @Expose val flag :String? = null,
    @Expose val name :String? = null,
    @Expose var region :String? = null,
    var nameRus:String? = null,
    var capitalRus:String? = null,
    var regionRus:String? = null
): Parcelable