package com.bartex.quizday.model.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TextEntity(
    @Expose val answer: String? = null,
    @Expose val question: String? = null,
    @Expose val value: Integer? = null,
    @Expose var category_id: Integer? = null,
    @Expose var category: CategoryEntity? = null,
    var isRight: Boolean = false
) : Parcelable