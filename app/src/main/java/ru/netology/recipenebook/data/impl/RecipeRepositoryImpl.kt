package ru.netology.recipenebook.data.impl

import androidx.lifecycle.map
import ru.netology.recipenebook.data.Recipe
import ru.netology.recipenebook.db.RecipeDao
import ru.netology.recipenebook.db.toEntity
import ru.netology.recipenebook.db.toModel
import ru.netology.recipenebook.repository.RecipeRepository

class RecipeRepositoryImpl(
    private val dao: RecipeDao
) : RecipeRepository {

    override var data = dao.getAll().map { entities ->
        entities.map { it.toModel() }
    }


    override fun getData() {
        data = dao.getAll().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun save(recipe: Recipe) {
        if (recipe.id == RecipeRepository.NEW_RECIPE_ID) dao.save(recipe = recipe.toEntity())
        else dao.updateContentById(
            recipe.id, recipe.title, recipe.author, recipe.content,
            recipe.type
        )
    }

    override fun update(recipe: Recipe) {
        save(recipe)
    }

    override fun delete(recipe: Recipe) {
        dao.removeById(recipe.id)
    }

    override fun favorite(long: Long) {
        dao.favById(long)
    }

    /**
    Filter Section
     */
    override fun showEuropean(type: String) {
        data = dao.getAll().map { entities ->
            entities.map { it.toModel() }.filter { it.type != type }
        }
    }

    override fun showAsian(type: String) {
        data = data.map {
            it.filter { it.type != type }
        }
    }

    override fun showPanasian(type: String) {
        data = data.map {
            it.filter { it.type != type }
        }
    }

    override fun showEastern(type: String) {
        data = data.map {
            it.filter { it.type != type }
        }
    }

    override fun showAmerican(type: String) {
        data = data.map {
            it.filter { it.type != type }
        }
    }


    override fun showRussian(type: String) {
        data = data.map {
            it.filter { it.type != type }
        }
    }


    override fun showMediterranean(type: String) {
        data = data.map {
            it.filter { it.type != type }
        }
    }
}
