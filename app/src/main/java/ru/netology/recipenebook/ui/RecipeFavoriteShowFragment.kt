package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.recipenebook.R
import ru.netology.recipenebook.adapter.RecipeAdapter
import ru.netology.recipenebook.databinding.ShowFavoriteBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class RecipeFavoriteShowFragment : Fragment() {

    private val viewModel by activityViewModels<RecipeViewModel>()
    private var _binding: ShowFavoriteBinding? = null
    private val binding get() = _binding
    private var isEmptyState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(
                context,
                context?.getString(R.string.fav_feeder_string1),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ShowFavoriteBinding.inflate(inflater)

        val adapter = RecipeAdapter(viewModel, RecipeAdapter.RECIPES_ADAPTER)
        binding?.favoriteList?.adapter = adapter

        viewModel.isFavouriteShow = true

        viewModel.allRecipesData.observe(viewLifecycleOwner) { recipes ->
            if (recipes.filter { it.isFavorite }.isEmpty())
                showEmptyStateFavorites()
            else if (isEmptyState) hideEmptyStateFavorites()

            adapter.submitList(recipes.filter { it.isFavorite })
        }

        viewModel.showRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            findNavController().navigate(R.id.toRecipeShowCertainFragment)
        }
        return binding?.root
    }

    private fun showEmptyStateFavorites() {
        if (binding == null) return
        with(binding!!) {
            // Hide RW and filter edit field
            favoriteList.visibility = View.GONE

            // SHow Empty State pic and text
            emptyIcon.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        }
        isEmptyState = true
    }

    private fun hideEmptyStateFavorites() {
        if (binding == null) return
        with(binding!!) {
            // Show RW and filter edit field
            favoriteList.visibility = View.VISIBLE

            // Hide Empty State pic and text
            emptyIcon.visibility = View.GONE
            emptyText.visibility = View.GONE
        }
        isEmptyState = false
    }
}