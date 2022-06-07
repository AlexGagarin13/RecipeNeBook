package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.recipenebook.R
import ru.netology.recipenebook.adapter.RecipeAdapter
import ru.netology.recipenebook.databinding.ShowFavoriteBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class RecipeFavoriteShowFragment : Fragment() {

    private val viewModel by activityViewModels<RecipeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ShowFavoriteBinding.inflate(layoutInflater, container, false).also { binding ->

        viewModel.data.observe(viewLifecycleOwner) { recipes ->

            val favRecipes = recipes.filter { it.isFavorite }.isEmpty()
            if (favRecipes) {
                binding.emptyText.isVisible = favRecipes
                binding.emptyTextMessage.isVisible = favRecipes
                binding.emptyIcon.isVisible = favRecipes
            }
        }

        val adapter = RecipeAdapter(viewModel)
        binding.favoriteList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val favRecipes = posts.filter { it.isFavorite }
            adapter.submitList(favRecipes)
        }

        binding.bottomNavBar.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.feed -> findNavController().popBackStack()
            }
            true
        }
    }.root
}