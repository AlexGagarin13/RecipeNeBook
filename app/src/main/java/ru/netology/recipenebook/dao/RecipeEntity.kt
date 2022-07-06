package ru.netology.recipenebook.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.netology.recipenebook.data.Recipe

@Entity(
    tableName = "recipes",
    foreignKeys = [ForeignKey(
        entity = RecCategoryEntity::class,
        parentColumns = ["id_cat"], childColumns = ["type"],
        onDelete = ForeignKey.SET_DEFAULT, onUpdate = ForeignKey.CASCADE
    )]
)
class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_rec")
    val id: Long,

    @ColumnInfo(name = "title_rec")
    val title: String,

    @ColumnInfo(name = "author_rec")
    val author: String,

    @ColumnInfo(name = "type")
    val type: Long = 0L,

    @ColumnInfo(name = "is_favorite_rec")
    val isFavorite: Boolean
)

internal fun RecipeEntity.toModel(): Recipe {
    return Recipe(id, title, author, type, isFavorite)
}

internal fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(id, title, author, type, isFavorite)
}