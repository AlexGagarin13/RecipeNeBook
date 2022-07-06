package ru.netology.recipenebook.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.recipenebook.R
import ru.netology.recipenebook.adapter.RecipeAdapter
import ru.netology.recipenebook.databinding.FeedRecipesBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class FeedRecipeFragment : Fragment() {

    private val viewModel by activityViewModels<RecipeViewModel>()
    private var _binding: FeedRecipesBinding? = null
    private val binding get() = _binding
    private var isEmptyState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(context, getString(R.string.rec_feeder_string1), Toast.LENGTH_LONG)
                .show()
            val exit = requireActivity().onBackPressedDispatcher.addCallback {
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FeedRecipesBinding.inflate(inflater, container, false)
        if (binding == null) return super.onCreateView(inflater, container, savedInstanceState)

        val adapter = RecipeAdapter(viewModel, RecipeAdapter.RECIPES_ADAPTER)

        binding?.list?.adapter = adapter
        val rw = binding?.list ?: return binding?.root
        adapter.attachRecyclerView(rw)

        viewModel.isFavouriteShow = false

        // set the filter string
        val filter = if (viewModel.recipeNamesFilter.value.isNullOrEmpty()) ""
        else viewModel.recipeNamesFilter.value
        binding?.recipeNameFilterEdit?.setText(filter)

        viewModel.catData.observe(viewLifecycleOwner) {
            viewModel.initCategories()
        }

        // Submit the list of recipes for the RW with the account of possible applied filter
        viewModel.recData.observe(viewLifecycleOwner) { recipes ->

            if (recipes.isEmpty())
                showEmptyState()
            else
                if (isEmptyState) hideEmptyState()

            val filterRN = viewModel.recipeNamesFilter.value
            if (filterRN.isNullOrEmpty()) {
                adapter.submitList(recipes) // No filter applied
            } else {
                adapter.submitList(recipes.filter { rec ->
                    rec.title.contains(filterRN, true) // Apply a filer for recipes names
                })
            }
        }
        // Update the displayed by RW list of Recipes with the search filter entered by the user
        viewModel.recipeNamesFilter.observe(viewLifecycleOwner) { rnFilter ->
            val recData = viewModel.recData.value
            if (rnFilter.isNullOrEmpty()) {
                adapter.submitList(recData)
            } else {
                adapter.submitList(recData?.filter { rec ->
                    rec.title.contains(rnFilter, true)
                })
            }
        }


        // Callback to monitor the filter text input by user
        binding?.recipeNameFilterEdit?.doOnTextChanged { text, start, before, count ->
            val newText = text.toString().trim()
            // if  (newText == null ) return@doOnTextChanged
            viewModel.setRecipeNamesFilter(newText)
            Log.d("doOnTextChanged", newText)
        }

        // Show recipe card fragment
        viewModel.showRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            findNavController().navigate(R.id.toRecipeShowCertainFragment)
        }

        // Show Recipe New Fragment
        binding?.fab?.alpha = 0.90f
        binding?.fab?.setOnClickListener {
            viewModel.addNewRecipe()

        }
        viewModel.editRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            findNavController().navigate(R.id.toRecipeCreateFragment)
        }

        if (viewModel.tempRecipe != null && viewModel.editRecipe.value != null) {
            findNavController().navigate(R.id.toRecipeCreateFragment)
        }

        return binding?.root
    }

    private fun showEmptyState() {
        if (binding == null) return
        with(binding!!) {
            // Hide RW and filter edit field
            list.visibility = View.GONE
            recipeNameFilterEdit.visibility = View.GONE

            // SHow Empty State pic and text
            emptyIcon.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        }
        isEmptyState = true
    }

    private fun hideEmptyState() {
        if (binding == null) return
        with(binding!!) {
            // Show RW and filter edit field
            list.visibility = View.VISIBLE
            recipeNameFilterEdit.visibility = View.VISIBLE

            // Hide Empty State pic and text
            emptyIcon.visibility = View.GONE
            emptyText.visibility = View.GONE
        }
        isEmptyState = false
    }
}

