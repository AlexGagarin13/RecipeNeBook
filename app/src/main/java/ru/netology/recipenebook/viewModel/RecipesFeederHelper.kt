package ru.netology.recipenebook.viewModel

import ru.netology.recipenebook.data.Recipe

interface RecipesFeederHelper {
    fun onRecipeClicked(recipe: Recipe?)
    fun getCategoryName(id: Long): String?
    fun onFavoriteClicked(recipe: Recipe?)
    fun updateRepoWithNewListFromTo(list: List<Recipe>, dragFrom: Int, dragTo: Int): Boolean
}