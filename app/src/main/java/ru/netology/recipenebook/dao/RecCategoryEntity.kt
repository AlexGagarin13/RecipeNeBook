package ru.netology.recipenebook.dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.recipenebook.data.RecipeCategory

@Entity(tableName = "types")
data class RecCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_cat")
    val id: Long,

    @ColumnInfo(name = "name_cat")
    val name: String,

    @ColumnInfo(name = "show_or_not")
    val showOrNot: Boolean
)


internal fun RecipeCategory.toEntity(): RecCategoryEntity {
    return RecCategoryEntity(id, name, showOrNot)
}

internal fun RecCategoryEntity.toModel(): RecipeCategory {
    return RecipeCategory(id, name, showOrNot)
}