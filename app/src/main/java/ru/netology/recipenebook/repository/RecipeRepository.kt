package ru.netology.recipenebook.repository

import androidx.lifecycle.LiveData
import ru.netology.recipenebook.data.Recipe

interface RecipeRepository {
    val data: LiveData<List<Recipe>>

    //CRUD logic
    fun save(recipe: Recipe)
    fun update(recipe: Recipe)
    fun delete(recipe: Recipe)
    fun favorite(long: Long)
    fun getData()

    //Filter Section
    fun showEuropean(type: String)
    fun showAsian(type: String)
    fun showPanasian(type: String)
    fun showEastern(type: String)
    fun showAmerican(type: String)
    fun showRussian(type: String)
    fun showMediterranean(type: String)


    //For correct ID generation
    companion object{
        const val NEW_RECIPE_ID = 0L
    }
}