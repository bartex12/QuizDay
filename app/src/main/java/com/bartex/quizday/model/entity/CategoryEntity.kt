package com.bartex.quizday.model.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryEntity (
    @Expose val title :String? = null,
    @Expose var clues_count :Integer? = null,
) : Parcelable