package ru.netology.recipenebook.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.recipenebook.adapter.RecipeInteractionListener
import ru.netology.recipenebook.data.Recipe
import ru.netology.recipenebook.data.impl.RecipeRepositoryImpl
import ru.netology.recipenebook.db.AppDb
import ru.netology.recipenebook.repository.RecipeRepository
import ru.netology.recipenebook.util.SingleLiveEvent

class RecipeViewModel(application: Application) : AndroidViewModel(application),
    RecipeInteractionListener {

    private val repository: RecipeRepository =
        RecipeRepositoryImpl(dao = AppDb.getInstance(context = application).recipeDao)

    //для получения get()
    val data by repository::data

    var filterIsActive:Boolean = false


    //SingleLiveEvents
    val navigateToShowFavorite = SingleLiveEvent<String>()
    val navigateToRecipeCreateScreenEvent = SingleLiveEvent<Unit>()
    val navigateToRecipeUpdateScreenEvent = SingleLiveEvent<String?>()
    val navigateToRecipeShowScreenEvent = SingleLiveEvent<Unit>()
    val navigateToRecipeFilterScreenEvent = SingleLiveEvent<Unit>()

    //Data
    val updateRecipe = MutableLiveData<Recipe>(null)
    val showRecipe = MutableLiveData<Recipe?>(null)
    private val currentRecipe = MutableLiveData<Recipe?>(null)

    /**
     * Filter Section
     */

    fun showEuropean(type: String) {
        repository.showEuropean(type)
        filterIsActive = true
    }

    fun showAsian(type: String) {
        repository.showAsian(type)
        filterIsActive = true
    }

    fun showPanasian(type: String) {
        repository.showPanasian(type)
        filterIsActive = true
    }

    fun showEastern(type: String) {
        repository.showEastern(type)
        filterIsActive = true
    }

    fun showAmerican(type: String) {
        repository.showAmerican(type)
        filterIsActive = true
    }

    fun showRussian(type: String) {
        repository.showRussian(type)
        filterIsActive = true
    }

    fun showMediterranean(type: String) {
        repository.showMediterranean(type)
        filterIsActive = true
    }

    fun clearFilter() {
        repository.getData()
    }


    override fun updateContent(
        id: Long,
        title: String,
        author: String,
        content: String,
        type: String
    ) {
        val recipe = Recipe(
            id = id,
            title = title,
            author = author,
            content = content,
            type = type
        )
        repository.save(recipe)
    }

    override fun onRemoveClicked(recipe: Recipe) {
        repository.delete(recipe)
    }

    override fun onEditClicked(recipe: Recipe) {
        // тут лежит обновляемый пост
        updateRecipe.value = recipe
        navigateToRecipeUpdateScreenEvent.call()
    }

    override fun onFavoriteClicked(recipeId: Long) {
        repository.favorite(recipeId)
    }


    override fun onCreateClicked() {
        navigateToRecipeCreateScreenEvent.call()
    }

    override fun onSaveClicked(title: String, author: String, content: String, type: String) {

        val recipe = Recipe(
            id = RecipeRepository.NEW_RECIPE_ID,
            title = title,
            author = author,
            content = content,
            type = type
        )
        repository.save(recipe)
        currentRecipe.value = null
    }

    override fun onShowRecipeClicked(recipe: Recipe) {
        showRecipe.value = recipe
        navigateToRecipeShowScreenEvent.call()
    }
}