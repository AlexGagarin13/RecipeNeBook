package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.recipenebook.R
import ru.netology.recipenebook.databinding.UpdateRecipeFragmentBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class RecipeUpdateFragment : Fragment() {

    private val viewModel by activityViewModels<RecipeViewModel>()
    private val args by navArgs<RecipeUpdateFragmentArgs>()
    private var type = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = UpdateRecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        render(binding)

        binding.rGroup.setOnCheckedChangeListener { _, i ->
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

        binding.saveButton.setOnClickListener {
            onSaveButtonClicked(binding)
        }
    }.root

    private fun onSaveButtonClicked(binding: UpdateRecipeFragmentBinding) {

        val id = args.initialContent!!.id
        val recipeName = binding.title.text.toString()
        val author = binding.author.text.toString()
        val cookingSteps = binding.content.text.toString()

        if (!emptyCheckUpdateWarning(title = recipeName,author = author, content = cookingSteps,type = type)) return

        viewModel.updateContent(
            id = id,
            title = recipeName,
            author = author,
            content = cookingSteps,
            type = type
        )
        findNavController().popBackStack()
    }

    private fun render(binding: UpdateRecipeFragmentBinding){
        binding.title.setText(args.initialContent?.title)
        binding.author.setText(args.initialContent?.author)
        binding.content.setText(args.initialContent?.content)
    }

    private fun emptyCheckUpdateWarning(
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