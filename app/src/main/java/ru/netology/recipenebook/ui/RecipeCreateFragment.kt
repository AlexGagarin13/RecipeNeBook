package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.recipenebook.R
import ru.netology.recipenebook.databinding.CreateRecipeFragmentBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class RecipeCreateFragment : Fragment() {
    private val viewModel by activityViewModels<RecipeViewModel>()
    var type = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = CreateRecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }

        binding.RGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.checkBoxEuropean -> type = "European"
                R.id.checkBoxAsian -> type = "Asian"
                R.id.checkBoxPanasian -> type = "Panasian"
                R.id.checkBoxEastern -> type = "Eastern"
                R.id.checkBoxAmerican -> type = "American"
                R.id.checkBoxRussian -> type = "Russian"
                R.id.checkBoxMediterranean -> type = "Mediterranean"
            }
        }
    }.root

    private fun onOkButtonClicked(binding: CreateRecipeFragmentBinding) {

        val title = binding.title.text.toString()
        val author = binding.author.text.toString()
        val content = binding.content.text.toString()

        if (!emptyCheckWarning(title, author, content, type)) return

        viewModel.onSaveClicked(title = title, author = author, content = content, type = type)
        findNavController().popBackStack()
    }

    private fun emptyCheckWarning(
        title: String,
        author: String,
        content: String,
        type: String
    ): Boolean {
        return if (title.isNullOrBlank() || author.isNullOrBlank() || content.isNullOrBlank() || type.isNullOrBlank()) {
            Toast.makeText(activity, "All fields must be filled", Toast.LENGTH_LONG).show()
            false
        } else true
    }
}