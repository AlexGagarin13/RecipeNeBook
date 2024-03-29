package ru.netology.recipenebook.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.callbackFlow
import ru.netology.recipenebook.R
import ru.netology.recipenebook.adapter.RecipeAdapter

import ru.netology.recipenebook.databinding.FeedRecipesBinding

import ru.netology.recipenebook.viewModel.RecipeViewModel

class FeedRecipeFragment : Fragment() {

    private val viewModel by activityViewModels<RecipeViewModel>()
    private var _binding: FeedRecipesBinding? = null
    private val binding get() = _binding


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

        val filter = if (viewModel.recipeNamesFilter.value.isNullOrEmpty()) ""
        else viewModel.recipeNamesFilter.value
        binding?.recipeNameFilterEdit?.setText(filter)

        viewModel.catData.observe(viewLifecycleOwner) {
            viewModel.initCategories()
        }

        viewModel.recData.observe(viewLifecycleOwner) { recipes ->

            val catToShow = viewModel.catData.value?.filter { cat ->
                cat.showOrNot
            }?.size

            if (catToShow == 7 && recipes.isEmpty()) {
                showEmptyState()
                hideEmptyCategory()
            } else if (catToShow != null) {
                if (catToShow < 7 && recipes.isEmpty()) {
                    showEmptyCategory()
                    hideEmptyState()
                }
            } else {
                hideEmptyState()
                hideEmptyCategory()
            }

            val filterRN = viewModel.recipeNamesFilter.value
            if (filterRN.isNullOrEmpty()) {
                adapter.submitList(recipes)
            } else {
                adapter.submitList(recipes.filter { rec ->
                    rec.title.contains(filterRN, true)
                })
            }
        }

        viewModel.recipeNamesFilter.observe(viewLifecycleOwner) { rnFilter ->
            val recData = viewModel.recData.value
            if (rnFilter.isNullOrEmpty()) {
                adapter.submitList(recData)
            } else {
                val filtered = recData?.filter { rec ->
                    rec.title.contains(rnFilter, true)
                }.orEmpty()
                if (filtered.isEmpty()) {
                    showEmptyCategory()
                } else {
                    hideEmptyCategory()
                    adapter.submitList(filtered)
                }
            }
        }

        binding?.recipeNameFilterEdit?.doOnTextChanged { text, _, _, _ ->
            val newText = text.toString().trim()
            viewModel.setRecipeNamesFilter(newText)
            Log.d("doOnTextChanged", newText)
        }

        viewModel.showRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            findNavController().navigate(R.id.toRecipeShowCertainFragment)
        }

        binding?.filter?.setOnClickListener {
            findNavController().navigate(R.id.categoriesFragment)
        }

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
            list.visibility = View.GONE
            recipeNameFilterEdit.visibility = View.GONE
            emptyIcon.visibility = View.VISIBLE
            emptyText.visibility = View.VISIBLE
        }
    }

    private fun hideEmptyState() {
        if (binding == null) return
        with(binding!!) {
            list.visibility = View.VISIBLE
            recipeNameFilterEdit.visibility = View.VISIBLE
            emptyIcon.visibility = View.GONE
            emptyText.visibility = View.GONE
        }
    }

    private fun showEmptyCategory() {
        if (binding == null) return
        with(binding!!) {
            list.visibility = View.GONE
            emptyFilter.visibility = View.VISIBLE
            emptyFilterText.visibility = View.VISIBLE
        }
    }

    private fun hideEmptyCategory() {
        if (binding == null) return
        with(binding!!) {
            list.visibility = View.VISIBLE
            emptyFilter.visibility = View.GONE
            emptyFilterText.visibility = View.GONE
        }
    }
}

