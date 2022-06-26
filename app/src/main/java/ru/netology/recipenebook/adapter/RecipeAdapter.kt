package ru.netology.recipenebook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.recipenebook.R
import ru.netology.recipenebook.data.Recipe
import ru.netology.recipenebook.databinding.RecipeBinding


class RecipeAdapter(
    private val interactionListener: RecipeInteractionListener

) : ListAdapter<Recipe, RecipeAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: RecipeBinding,
        listener: RecipeInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: Recipe

        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.options).apply {
                inflate(R.menu.option_menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClicked(recipe)
                            true
                        }
                        R.id.edit -> {
                            listener.onEditClicked(recipe)
                            true
                        }
                        else -> false
                    }
                }
            }
        }

        fun bind(recipe: Recipe) {
            this.recipe = recipe
            with(binding) {
                title.text = recipe.title
                author.text = recipe.author
                type.text = recipe.type
                content.text = recipe.content
                favoriteButton.setImageResource(getLikeIconResId(recipe.isFavorite))

                favoriteButton.setOnClickListener {
                    interactionListener.onFavoriteClicked(recipe.id)
                }

                title.setOnClickListener {
                    interactionListener.onShowRecipeClicked(recipe)
                }

                author.setOnClickListener {
                    interactionListener.onShowRecipeClicked(recipe)
                }

                content.setOnClickListener {
                    interactionListener.onShowRecipeClicked(recipe)
                }

                type.setOnClickListener {
                    interactionListener.onShowRecipeClicked(recipe)
                }

                options.setOnClickListener {
                    popupMenu.show()
                }
            }
        }

        @DrawableRes
        private fun getLikeIconResId(liked: Boolean) =
            if (liked) R.drawable.ic_favorites_24dp else R.drawable.ic_favorites_border_24dp
    }
}

private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {

    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
        oldItem == newItem
}