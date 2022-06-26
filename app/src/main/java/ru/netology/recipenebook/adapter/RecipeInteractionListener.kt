package ru.netology.recipenebook.adapter

import ru.netology.recipenebook.data.Recipe


interface RecipeInteractionListener {
    fun onRemoveClicked(recipe: Recipe)
    fun onEditClicked(recipe: Recipe)
    fun onFavoriteClicked(recipeId: Long)
    fun onCreateClicked()
    fun onSearchClicked(text: String)
    fun updateContent(id: Long, title: String, author: String, content: String, type: String)
    fun onSaveClicked(title: String, author: String, content: String, type: String)
    fun onShowRecipeClicked(recipe: Recipe)
}