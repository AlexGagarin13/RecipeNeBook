package ru.netology.recipenebook.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAll(): LiveData<List<RecipeEntity>>

    @Insert
    fun save(recipe: RecipeEntity)

    @Query("UPDATE recipes SET " +
            "title = :content," +
            " author = :title, " +
            "type = :type," +
            " content = :author" +
            " WHERE id = :id")
    fun updateContentById(
        id: Long, content: String, title: String,
        author: String, type: String)

    @Query(
        """
        UPDATE recipes SET
        isFavorite = CASE WHEN isFavorite THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun favById(id: Long)

    @Query("DELETE FROM recipes WHERE id = :id")
    fun removeById(id: Long)

    //Filter Section
    @Query("SELECT * FROM recipes WHERE type = :type")
    fun getEuropean(type: String): LiveData<List<RecipeEntity>>

}