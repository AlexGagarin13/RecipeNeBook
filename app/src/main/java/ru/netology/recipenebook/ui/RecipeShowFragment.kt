package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.recipenebook.R
import ru.netology.recipenebook.adapter.StepsAdapter
import ru.netology.recipenebook.databinding.RecipeBinding
import ru.netology.recipenebook.databinding.ShowCertainRecipeBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class RecipeShowFragment : Fragment() {

    private val viewModel by activityViewModels<RecipeViewModel>()
    private var _binding: RecipeBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val getRecipe = viewModel.showRecipe.value ?: return super.onCreateView(
            inflater,
            container,
            savedInstanceState
        )
        val recipe = viewModel.getRecipeById(getRecipe.id)

        _binding = RecipeBinding.inflate(inflater, container, false)

        val adapter = StepsAdapter(viewModel, StepsAdapter.SHOW_ADAPTER)

        binding?.title?.text = recipe.title
        binding?.author?.text = recipe.author
        binding?.type?.text = viewModel.getCatNameId(recipe.type)
        binding?.favoriteButton?.isChecked = recipe.isFavorite

        binding?.stepsList?.adapter = adapter

        viewModel.stepsAllData.observe(viewLifecycleOwner) { steps ->
            adapter.submitList(steps.filter { it.recipeId == recipe.id })
        }

        if (!viewModel.isFavouriteShow) {
            binding?.imageMore?.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.recipe_card_more)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.recipe_edit -> {
                                viewModel.onEditRecipe(recipe)
                                true
                            }
                            R.id.recipe_remove -> {
                                MaterialAlertDialogBuilder(it.context)
                                    .setMessage(getString(R.string.recipe_card_string1))
                                    .setNegativeButton(getString(R.string.recipe_card_string2)) { dialog, which -> }
                                    .setPositiveButton(getString(R.string.recipe_card_string3)) { dialog, which ->
                                        viewModel.deleteRecipe(recipe)
                                        findNavController().popBackStack()
                                    }.show()
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        } else {
            binding?.imageMore?.isVisible = false
        }

        viewModel.editRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            findNavController().navigate(R.id.toRecipeCreateFragment)
        }

//        viewModel.navigateToRecipeEditScreen.observe(viewLifecycleOwner) {
//            parentFragmentManager.commit {
//                addToBackStack(null)
//                replace(R.id.app_fragment_container, RecipeEditFragment())
//            }
//        }

        binding?.favoriteButton?.setOnClickListener {
            val button = it as MaterialButton
            viewModel.setFavorite(recipe.id, button.isChecked)
        }

        setFragmentResultListener(RecipeCreateFragment.REQUEST_KEY) { requestKey, bundle ->
            if (requestKey != RecipeCreateFragment.REQUEST_KEY) return@setFragmentResultListener
            val result =
                bundle.getString(RecipeCreateFragment.RESULT_KEY) ?: return@setFragmentResultListener
            if (result != RecipeCreateFragment.RESULT_VALUE) return@setFragmentResultListener

            val updateRecipe = viewModel.getRecipeById(getRecipe.id)
            binding?.title?.text = updateRecipe.title
            binding?.author?.text = updateRecipe.author
            binding?.type?.text = viewModel.getCatNameId(updateRecipe.type)
            binding?.favoriteButton?.isChecked = updateRecipe.isFavorite
        }
        return binding?.root
    }
}