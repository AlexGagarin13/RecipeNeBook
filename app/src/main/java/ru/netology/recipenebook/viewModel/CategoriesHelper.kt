package ru.netology.recipenebook.viewModel

interface CategoriesHelper {
    fun getNumberOfSelectedCategories(): Int
    fun setCategoryVisible(id: Long)
    fun setCategoryInvisible(id: Long)
}