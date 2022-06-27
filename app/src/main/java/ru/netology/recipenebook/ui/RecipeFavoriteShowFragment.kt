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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeUpdateScreenEvent.observe(this) {
            val updatedRecipe = viewModel.updateRecipe.value
            val directions = FeedRecipeFragmentDirections.toUpdateRecipeFragment(updatedRecipe)
            findNavController().navigate(directions)
        }

        viewModel.navigateToRecipeShowScreenEvent.observe(this) {
            val viewRecipe = viewModel.showRecipe.value
            val directions = FeedRecipeFragmentDirections.toRecipeShowCertainFragment(viewRecipe)
            findNavController().navigate(directions)
        }

        viewModel.navigateToRecipeFilterScreenEvent.observe(this) {
            val directions = FeedRecipeFragmentDirections.toFilterFragment()
            findNavController().navigate(directions)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ShowFavoriteBinding.inflate(layoutInflater, container, false).also { binding ->

        viewModel.data.observe(viewLifecycleOwner) { recipes ->

            val favRecipes = recipes.none { it.isFavorite }
            if (favRecipes) {
                binding.emptyText.isVisible = favRecipes
                binding.emptyIcon.isVisible = favRecipes
            }
        }

        val adapter = RecipeAdapter(viewModel)
        binding.favoriteList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            val favRecipes = recipes.filter { it.isFavorite }
            adapter.submitList(favRecipes)
        }

        binding.bottomNavBar.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.feed -> findNavController().popBackStack()
            }
            true
        }

        binding.bottomNavBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorites -> {
                    viewModel.navigateToShowFavorite.call()
                    true
                }
                R.id.filter -> {
                    viewModel.navigateToRecipeFilterScreenEvent.call()
                    true
                }
                R.id.feed -> {
                    viewModel.feedFragment.observe(viewLifecycleOwner) {
                        findNavController().popBackStack()
                    }
                    true
                }
                else -> false
            }
        }
    }.root
}