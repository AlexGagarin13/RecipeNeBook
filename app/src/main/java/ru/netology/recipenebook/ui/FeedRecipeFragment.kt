package ru.netology.recipenebook.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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

        if (viewModel.filterIsActive) {
            binding.clearFilterButton.isVisible = viewModel.filterIsActive
            binding.clearFilterButton.setOnClickListener {
                viewModel.clearFilter()
                viewModel.filterIsActive = false
                binding.clearFilterButton.visibility = View.GONE
                viewModel.data.observe(viewLifecycleOwner) { recipes ->
                    adapter.submitList(recipes)
                }
            }
        } else {
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {

                    if (newText.isNotBlank()) {
                        viewModel.onSearchClicked(newText)
                        viewModel.data.observe(viewLifecycleOwner) { recipe ->
                            adapter.submitList(recipe)
                        }
                    }
                    if (TextUtils.isEmpty(newText)) {
                        viewModel.clearFilter()
                        viewModel.data.observe(viewLifecycleOwner) { recipe ->
                            adapter.submitList(recipe)
                        }
                    }
                    return false
                }
            })
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
                else -> false
            }
        }

        binding.fab.setOnClickListener {
            viewModel.onCreateClicked()
        }
    }.root
}

