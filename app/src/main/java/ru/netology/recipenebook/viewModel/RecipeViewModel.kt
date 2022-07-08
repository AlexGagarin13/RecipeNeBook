package ru.netology.recipenebook.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import ru.netology.recipenebook.data.Recipe
import ru.netology.recipenebook.data.RecipeCategory
import ru.netology.recipenebook.data.RecipeStep
import ru.netology.recipenebook.data.impl.CategoryRepositoryImplementation
import ru.netology.recipenebook.data.impl.RecipeRepositoryImplementation
import ru.netology.recipenebook.data.impl.RecipeStepsRepositoryImplementation
import ru.netology.recipenebook.db.AppDb
import ru.netology.recipenebook.repository.CategoryRepository
import ru.netology.recipenebook.repository.RecipeRepository
import ru.netology.recipenebook.repository.RecipeStepsRepository
import ru.netology.recipenebook.util.SingleLiveEvent
import java.io.IOException
import kotlin.math.max
import kotlin.math.min


const val NEW_ITEM_ID = 0L

class RecipeViewModel(private val inApplication: Application) :
    AndroidViewModel(inApplication), RecipesFeederHelper, StepsDetailsHelper, CategoriesHelper {

    var tempBitMap: Bitmap? = null

    var isNewRecipe: Boolean = false
    var isNewStep: Boolean = false
    var selectedSpinner: String? = "empty"
    var isFavouriteShow: Boolean = false


    private val categoriesRepo: CategoryRepository =
        CategoryRepositoryImplementation(AppDb.getInstance(inApplication).categoryDao)
    val catData by categoriesRepo::data
    fun saveCategory(category: RecipeCategory) = categoriesRepo.save(category)
    fun getCatNameId(id: Long) = categoriesRepo.getName(id)

    private val recipesRepo: RecipeRepository =
        RecipeRepositoryImplementation(AppDb.getInstance(inApplication).recipeDao)
    val recData by recipesRepo::data
    val allRecipesData by recipesRepo::allData
    val recipeNamesFilter = SingleLiveEvent<String?>()
    val showRecipe = SingleLiveEvent<Recipe?>()
    val favouriteRecipe = SingleLiveEvent<Recipe?>()
    val editRecipe = SingleLiveEvent<Recipe?>()
    var tempRecipe: Recipe? = null
    fun saveRecipe(recipe: Recipe) = recipesRepo.save(recipe)
    fun deleteRecipe(recipe: Recipe) = recipesRepo.delete(recipe.id)

    private val recStepsRepo: RecipeStepsRepository =
        RecipeStepsRepositoryImplementation(AppDb.getInstance(inApplication).recStepsDao)
    val stepsAllData by recStepsRepo::dataAll
    val editedStepsList: MutableList<RecipeStep> = mutableListOf()
    private var editStep: RecipeStep? = null
    var stepUri: Uri? = null
    fun saveStep(step: RecipeStep) = recStepsRepo.save(step)
    fun removeStep(step: RecipeStep) = recStepsRepo.delete(step.id)
    fun clearEditedStep() {
        editStep = null
    }

    fun setEditedStepsPicture(picUri: Uri?) {
        val step = chooseThePicture.value ?: return
        if (picUri == null) return

        stepUri = picUri

        val inputStream = inApplication.contentResolver.openInputStream(picUri)
        val drawable = Drawable.createFromStream(inputStream, stepUri.toString())
        val bitmap = (drawable as BitmapDrawable).bitmap

        val timeMills = System.currentTimeMillis()
        val fileName = "picture_$timeMills.jpg"
        val fos = inApplication.openFileOutput(fileName, Context.MODE_PRIVATE)

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        editStep = step.copy(picture = fileName, pUri = picUri)
        saveStep(editStep!!)
    }

    val navigateToNewStepEdit = SingleLiveEvent<Unit>()
    val chooseThePicture = SingleLiveEvent<RecipeStep?>()

    override fun onChoosePictureClicked(step: RecipeStep) {
        chooseThePicture.value = step
    }

    override fun onEditStepContents(stepId: Long, text: String) {
        recStepsRepo.updateContent(stepId, text)
    }

    override fun updateStep(step: RecipeStep) {
        editedStepsList.add(step)
        recStepsRepo.updateStep(step)
    }

    override fun editStep(step: RecipeStep) {
        editStep = step
        isNewStep = false
        navigateToNewStepEdit.call()
    }

    override fun getBitmapFromFile(name: String): Bitmap? {
        val file = inApplication.filesDir.resolve(name)
        if (!file.exists()) return null
        val fis = inApplication.openFileInput(name)
        val bm = BitmapFactory.decodeStream(fis)
        fis.close()
        return bm
    }

    fun setRecipeNamesFilter(newText: String) {
        recipeNamesFilter.value = newText
    }

    fun addNewRecipe() {
        editedStepsList.clear()
        val newRecipe = Recipe(NEW_ITEM_ID, "", "", 1L, false)
        val recID = saveRecipe(newRecipe)

        editRecipe.value = newRecipe.copy(id = recID)
        isNewRecipe = true
    }

    fun onEditRecipe(recipe: Recipe) {
        editedStepsList.clear()
        editRecipe.value = recipe
        isNewRecipe = false
    }

    override fun onRecipeClicked(recipe: Recipe?) {
        showRecipe.value = recipe
    }

    override fun onFavoriteClicked(recipe: Recipe?) {
        favouriteRecipe.value = recipe
    }

    override fun getCategoryName(id: Long) = getCatNameId(id)


    override fun updateRepoWithNewListFromTo(
        list: List<Recipe>,
        dragFrom: Int,
        dragTo: Int
    ): Boolean {

        val upDownMovement = dragFrom < dragTo
        val downUpMovement = dragFrom > dragTo

        val minIndex = min(dragFrom, dragTo)
        val maxIndex = max(dragFrom, dragTo)
        val rwSubList = list.subList(minIndex, maxIndex + 1)
        val inListIds = rwSubList.map { it.id }.sorted() //order of IDs in db

        if (inListIds.size != rwSubList.size) return false

        val updatedList = mutableListOf<Recipe>()

        if (upDownMovement) {
            val rwlSize = rwSubList.size
            val firstToLast = rwSubList[0]
            rwSubList.forEachIndexed { index, recipe ->
                if (index == rwlSize - 1) {
                    updatedList.add(firstToLast)
                } else {
                    updatedList.add(rwSubList[index + 1])
                }
            }
        }

        if (downUpMovement) {
            val rwlSize = rwSubList.size
            val lastToFirst = rwSubList[rwlSize - 1]
            rwSubList.forEachIndexed { index, recipe ->
                if (index == 0) {
                    updatedList.add(lastToFirst)
                } else {
                    updatedList.add(rwSubList[index - 1])
                }
            }
        }

        val listOfLists: MutableList<List<RecipeStep>> = mutableListOf()
        updatedList.forEach { rwRecipe ->
            listOfLists.add(recStepsRepo.getStepsListWithRecId(rwRecipe.id))
        }

        updatedList.forEachIndexed { index, rwRecipe ->
            val rec = getRecipeById(inListIds[index])
            val updatedRec = rwRecipe.copy(id = rec.id)
            recipesRepo.update(updatedRec)

            val stepsList = listOfLists[index]
            stepsList.forEach { step ->
                val updatedStep = step.copy(recipeId = updatedRec.id)
                recStepsRepo.updateStep(updatedStep)
            }
        }
        return true
    }

    override fun updateStepsRepoWithListFromTo(list: List<RecipeStep>, dragFrom: Int, dragTo: Int) {

        val upDownMovement = dragFrom < dragTo
        val downUpMovement = dragFrom > dragTo

        val minIndex = min(dragFrom, dragTo)
        val maxIndex = max(dragFrom, dragTo)
        val rwSubList = list.subList(minIndex, maxIndex + 1)
        val inListIds = rwSubList.map { it.id }.sorted()

        if (inListIds.size != rwSubList.size) return

        val updatedList = mutableListOf<RecipeStep>()

        if (upDownMovement) {
            val rwlSize = rwSubList.size
            val firstToLast = rwSubList[0]
            rwSubList.forEachIndexed { index, _ ->
                if (index == rwlSize - 1) {
                    updatedList.add(firstToLast)
                } else {
                    updatedList.add(rwSubList[index + 1])
                }
            }
        }

        if (downUpMovement) {
            val rwlSize = rwSubList.size
            val lastToFirst = rwSubList[rwlSize - 1]
            rwSubList.forEachIndexed { index, _ ->
                if (index == 0) {
                    updatedList.add(lastToFirst)
                } else {
                    updatedList.add(rwSubList[index - 1])
                }
            }
        }

        updatedList.forEachIndexed { index, rwStep ->
            val step = getStepById(inListIds[index])
            val updatedStep = rwStep.copy(id = step.id)
            recStepsRepo.updateStep(updatedStep)
        }

    }

    fun addNewEditedStep() {
        val recId = editRecipe.value?.id ?: return
        val step = RecipeStep(NEW_ITEM_ID, recId, "")
        isNewStep = true
        val stepId = saveStep(step)
        editStep = step.copy(id = stepId)
        navigateToNewStepEdit.call()
    }

    override fun deleteEditedStep(step: RecipeStep) {
        val oldvalue = editStep
        editStep = step
        deleteEditedStepPicture()
        recStepsRepo.delete(step.id)

        editStep = oldvalue
    }

    override fun getNumberOfSelectedCategories(): Int {
        return categoriesRepo.getNumberOfSelectedCategories()
    }

    override fun setCategoryVisible(id: Long) {
        categoriesRepo.setVisible(id)
    }

    override fun setCategoryInvisible(id: Long) {
        categoriesRepo.setNotVisible(id)
    }

    fun setFavorite(id: Long, favourite: Boolean) {
        recipesRepo.favorite(id, favourite)
    }

    fun onSaveEditedRecipe(recipe: Recipe) {
        val recId = saveRecipe(recipe)

        val stepsList = stepsAllData.value?.filter { it.recipeId == recId }

        stepsList?.forEach { step ->
            updateStep(step)
        }

        editRecipe.value = null
        tempRecipe = null
    }

    fun getCatIdbyName(category: String?): Long? {
        return categoriesRepo.getIdByName(category)
    }

    fun getEditedRecipe(): Recipe? {
        return editRecipe.value
    }

    fun initCategories() {

        val size = getNumberOfSelectedCategories()

        if (size > 0) return

        saveCategory(RecipeCategory(NEW_ITEM_ID, "Европейская"))
        saveCategory(RecipeCategory(NEW_ITEM_ID, "Азиатская"))
        saveCategory(RecipeCategory(NEW_ITEM_ID, "Паназиатская"))
        saveCategory(RecipeCategory(NEW_ITEM_ID, "Восточная"))
        saveCategory(RecipeCategory(NEW_ITEM_ID, "Американская"))
        saveCategory(RecipeCategory(NEW_ITEM_ID, "Русская"))
        saveCategory(RecipeCategory(NEW_ITEM_ID, "Средиземноморская"))
    }

    fun getEditedStep(): RecipeStep? {
        return editStep
    }

    fun onSaveEditedStep(newStep: RecipeStep) {
        saveStep(newStep)
        editStep = null
    }

    fun getRecipeById(id: Long): Recipe {
        return recipesRepo.getRecipeById(id)
    }

    fun getStepById(stepId: Long): RecipeStep {
        return recStepsRepo.getStepById(stepId)
    }

    fun deleteEditedStepPicture() {
        val step = editStep
        val name = step?.picture ?: return
        if (name == "empty") return

        val file = inApplication.filesDir.resolve(name)
        if (file.exists()) {
            file.delete()
        }
        editStep = step.copy(picture = "empty", pUri = null)
        saveStep(editStep!!)
    }

    fun saveTempBitmapToFile() {
        if (tempBitMap == null) return
        val step = editStep ?: return

        val bitmap = tempBitMap

        val timeMills = System.currentTimeMillis()
        val fileName: String = "picture_$timeMills.jpg"
        val fos = inApplication.openFileOutput(fileName, Context.MODE_PRIVATE)

        try {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        editStep = step.copy(picture = fileName, pUri = null)
        saveStep(editStep!!)
        tempBitMap = null
    }
}