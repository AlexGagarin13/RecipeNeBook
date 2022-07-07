package ru.netology.recipenebook.data

data class RecipeCategory(
    val id: Long = 0L,
    val name: String,
    val showOrNot: Boolean = true
)