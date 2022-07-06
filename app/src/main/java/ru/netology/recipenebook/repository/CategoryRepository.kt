package ru.netology.recipenebook.repository

import androidx.lifecycle.LiveData
import ru.netology.recipenebook.data.RecipeCategory


interface CategoryRepository {
    val data: LiveData<List<RecipeCategory>>

    fun save(cat: RecipeCategory): Long
    fun delete(id: Long)
    fun setVisible(id: Long)
    fun setNotVisible(id: Long)
    fun getName(getId: Long): String?
    fun getIdByName(category: String?): Long?
    fun getNumberOfSelectedCategories(): Int
    fun deleteAllCategories()
}