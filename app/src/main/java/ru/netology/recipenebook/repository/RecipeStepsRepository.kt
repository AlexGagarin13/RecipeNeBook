package ru.netology.recipenebook.repository

import androidx.lifecycle.LiveData
import ru.netology.recipenebook.data.RecipeStep

interface RecipeStepsRepository {
    val dataAll: LiveData<List<RecipeStep>>
    val dataFiltered: LiveData<List<RecipeStep>>

    fun save(step: RecipeStep): Long
    fun delete(id: Long)
    fun update(oldId: Long, newId: Long)
    fun updateContent(stepId: Long, newContent: String)
    fun updateStep(step: RecipeStep)
    fun getStepById(getId: Long): RecipeStep
    fun getStepsListWithRecId(getRecId: Long): List<RecipeStep>
}