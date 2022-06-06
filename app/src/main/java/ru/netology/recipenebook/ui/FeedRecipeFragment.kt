package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.netology.recipenebook.adapter.PostsAdapter
import ru.netology.recipenebook.databinding.FeedRecipesBinding

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

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            if (!viewModel.filterIsActive) {
                binding.emptyText.isVisible = recipes.isEmpty()
                binding.emptyTextMessage.isVisible = recipes.isEmpty()
                binding.emptyIcon.isVisible = recipes.isEmpty()
            }
        }

        val adapter = PostsAdapter(viewModel)
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
        }

        //Filter button visibility logic
        if (viewModel.filterIsActive) {
            binding.clearFilterButton.isVisible = viewModel.filterIsActive
            binding.clearFilterButton.setOnClickListener {
                viewModel.filterIsActive = false
                viewModel.clearFilter()
                binding.clearFilterButton.isVisible = false
                viewModel.data.observe(viewLifecycleOwner) { posts ->
                    adapter.submitList(posts)
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
                else -> false
            }
        }
    }.root
}

