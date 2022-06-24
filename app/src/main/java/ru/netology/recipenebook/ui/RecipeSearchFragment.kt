package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.recipenebook.databinding.SearchRecipesFragmentBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class RecipeSearchFragment : Fragment() {
    private val viewModel by activityViewModels<RecipeViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = SearchRecipesFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        binding.applySearchBtn.setOnClickListener {
            onSearchBtnClicked(binding)
        }
    }.root


    private fun onSearchBtnClicked(binding: SearchRecipesFragmentBinding) {
        val searchQuery = binding.searchField.text
        if (searchQuery.isNullOrBlank()) {
            Toast.makeText(activity, "We can't search nothing", Toast.LENGTH_LONG).show()
            return
        }
        viewModel.searchRecipeByTitle(searchQuery.toString())
        findNavController().popBackStack()
    }
}