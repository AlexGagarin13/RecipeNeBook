package ru.netology.recipenebook.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

data class Recipe(
    val id: Long,
    val title: String,
    val author: String,
    val type: Long,
    val isFavorite: Boolean,
)