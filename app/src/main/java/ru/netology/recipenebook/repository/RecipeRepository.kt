package ru.netology.recipenebook.repository

import androidx.lifecycle.LiveData
import ru.netology.recipenebook.data.Recipe

interface RecipeRepository {
    val data: LiveData<List<Recipe>>
    val allData: LiveData<List<Recipe>>

    fun save(recipe: Recipe): Long
    fun delete(id: Long)
    fun clearAll()
    fun favorite(id: Long, favourite: Boolean)
    fun getRecipeById(getId: Long): Recipe
    fun update(recipe: Recipe): Int
    fun getRecipesList(ids: List<Long>): List<Recipe>
    fun listAllRecipes(): List<Recipe>
    fun listAllSelectedRecipes(): List<Recipe>
}