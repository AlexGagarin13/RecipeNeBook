package ru.netology.recipenebook.data.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import ru.netology.recipenebook.dao.CategoryDao
import ru.netology.recipenebook.dao.toEntity
import ru.netology.recipenebook.dao.toModel
import ru.netology.recipenebook.data.RecipeCategory
import ru.netology.recipenebook.repository.CategoryRepository

class CategoryRepositoryImplementation(private val dao: CategoryDao) : CategoryRepository {
    private var categories: List<RecipeCategory> = emptyList()
        get() = checkNotNull(data.value) { "Categories data value should not be null!" }

    override lateinit var data: LiveData<List<RecipeCategory>>

    init {
        data = dao.getAllSortedAsc().asLiveData().map { catList ->
            catList.map { it.toModel() }
        }
    }

    override fun getNumberOfSelectedCategories(): Int {
        return dao.getNumberOfSelectedTypes()
    }

    override fun deleteAllCategories() {
        dao.deleteAllTypes()
    }

    override fun getIdByName(category: String?): Long? {
        return dao.getIdByName(category)
    }

    override fun save(cat: RecipeCategory): Long {
        return dao.insert(cat.toEntity())
    }

    override fun delete(id: Long) {
        dao.removeById(id)
    }

    override fun setVisible(id: Long) {
        dao.setVisible(id)
    }

    override fun setNotVisible(id: Long) {
        dao.setNotVisible(id)
    }

    override fun getName(getId: Long): String? = dao.getNameById(getId)

}