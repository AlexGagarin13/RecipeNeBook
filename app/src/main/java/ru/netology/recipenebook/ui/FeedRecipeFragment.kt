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
import ru.netology.recipenebook.databinding.FeedRecipesBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class FeedRecipeFragment : Fragment(R.layout.feed_recipes) {

    private val viewModel by activityViewModels<RecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToShowFavorite.observe(this) {
            val direction = FeedRecipeFragmentDirections.toFavoriteShowFragment()
            findNavController().navigate(direction)
        }

        //Update
        viewModel.navigateToRecipeUpdateScreenEvent.observe(this) {
            val updatedRecipe = viewModel.updateRecipe.value
            val directions = FeedRecipeFragmentDirections.toUpdateRecipeFragment(updatedRecipe)
            findNavController().navigate(directions)
        }

        //Create
        viewModel.navigateToRecipeCreateScreenEvent.observe(this) {
            val directions = FeedRecipeFragmentDirections.toRecipeCreateFragment()
            findNavController().navigate(directions)
        }

        //ShowRecipeCard
        viewModel.navigateToRecipeShowScreenEvent.observe(this) {
            val showRecipe = viewModel.showRecipe.value
            val directions = FeedRecipeFragmentDirections.toRecipeShowCertainFragment(showRecipe)
            findNavController().navigate(directions)
        }

        //Filter
        viewModel.navigateToRecipeFilterScreenEvent.observe(this) {
            val directions = FeedRecipeFragmentDirections.toFilterFragment()
            findNavController().navigate(directions)
        }

        //Search
        viewModel.navigateToRecipeSearchScreenEvent.observe(this) {
            val directions = FeedRecipeFragmentDirections.toRecipeSearchFragment()
            findNavController().navigate(directions)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FeedRecipesBinding.inflate(layoutInflater, container, false).also { binding ->

        val adapter = RecipeAdapter(viewModel)
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
        }

        //Search hints (buttons) and Filters visibility logic
        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            if (!viewModel.filterIsActive) {
                binding.hintGroup.isVisible = recipes.isEmpty()
            }
            if (viewModel.firstRunApp) {
                binding.clearFilterButton.isVisible = false
                binding.clearSearchButton.isVisible = false
            }
            if (viewModel.searchIsActive) {
                binding.clearSearchButton.isVisible = true
            }
            if (viewModel.filterIsActive) {
                binding.clearFilterButton.isVisible = true
            }
        }
        //Filter button visibility logic
        if (viewModel.filterIsActive) {
            binding.clearFilterButton.isVisible = viewModel.filterIsActive
            binding.clearFilterButton.setOnClickListener {
                viewModel.clearFilter()
                viewModel.filterIsActive = false
                binding.clearFilterButton.isVisible = false
                viewModel.data.observe(viewLifecycleOwner) { recipes ->
                    adapter.submitList(recipes)
                }
            }
        }

        //Search feature visibility logic
        if (viewModel.searchIsActive) {
            binding.clearSearchButton.isVisible = viewModel.searchIsActive
            binding.clearSearchButton.setOnClickListener {
                viewModel.clearFilter()
                viewModel.searchIsActive = false
                binding.hintGroup.isVisible = false
                viewModel.data.observe(viewLifecycleOwner) { recipes ->
                    adapter.submitList(recipes)
                }
            }
        }

        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.favorites -> viewModel.navigateToShowFavorite.call()
            }
            true
        }

        binding.fab.setOnClickListener {
            viewModel.onCreateClicked()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filter -> {
                    viewModel.navigateToRecipeFilterScreenEvent.call()
                    true
                }
                R.id.search -> {
                    viewModel.navigateToRecipeSearchScreenEvent.call()
                    true
                }
                else -> false
            }
        }
    }.root
}

