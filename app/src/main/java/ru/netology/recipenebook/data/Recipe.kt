package ru.netology.recipenebook.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Recipe(
    val id: Long,
    val title: String,
    val author: String,
    val content: String,
    val type: String,
    val isFavorite: Boolean = false,
): Parcelable