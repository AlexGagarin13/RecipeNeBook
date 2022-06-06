package ru.netology.recipenebook.db

import ru.netology.recipenebook.data.Recipe

internal fun RecipeEntity.toModel() = Recipe(
    id = id,
    title = title,
    author = author,
    content = content,
    type = type,
    isFavorite = isFavorite,
)

internal fun Recipe.toEntity() = RecipeEntity(
    id = id,
    title = title,
    author = author,
    content = content,
    type = type,
    isFavorite = isFavorite,
)