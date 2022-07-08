package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.recipenebook.R
import ru.netology.recipenebook.adapter.StepsAdapter
import ru.netology.recipenebook.data.Recipe
import ru.netology.recipenebook.databinding.CreateRecipeFragmentBinding

import ru.netology.recipenebook.viewModel.NEW_ITEM_ID
import ru.netology.recipenebook.viewModel.RecipeViewModel

const val IMAGE_PICK_KEY = 10001

class RecipeCreateFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private val viewModel by activityViewModels<RecipeViewModel>()
    private var _binding: CreateRecipeFragmentBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            goBackWithDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_new_options, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.recipe_new_options_save -> {
                with(binding!!) {
                    val curEditRecipe = viewModel.getEditedRecipe() ?: return false
                    val stepsList =
                        viewModel.stepsAllData.value?.filter { it.recipeId == curEditRecipe.id }

                    if (title.text.toString().isBlank() || author.text.toString()
                            .isBlank() || stepsList?.size == 0
                    ) {
                        Toast.makeText(
                            context,
                            getString(R.string.recipe_new_string1),
                            Toast.LENGTH_SHORT
                        )
                            .also {
                                it.setGravity(
                                    Gravity.CENTER_VERTICAL,
                                    Gravity.AXIS_X_SHIFT,
                                    Gravity.AXIS_Y_SHIFT
                                )
                            }
                            .show()
                        return true
                    }

                    val selectedSpinner = viewModel.selectedSpinner
                    val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
                    val recipeSave = viewModel.getEditedRecipe()?.copy(
                        title = title.text.toString(),
                        author = author.text.toString(), type = catId,
                        isFavorite = favoriteButton.isChecked
                    ) ?: return false

                    author.requestFocus()

                    viewModel.onSaveEditedRecipe(recipeSave)
                }

                val resultBundle = Bundle(1)
                val content = RESULT_VALUE
                resultBundle.putString(RESULT_KEY, content)
                setFragmentResult(REQUEST_KEY, resultBundle)

                findNavController().popBackStack()
                true
            }

            R.id.recipe_new_options_discard -> {
                goBackWithDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreateRecipeFragmentBinding.inflate(inflater, container, false)

        val recipe = viewModel.getEditedRecipe() ?: return binding?.root
        var selectedSpinner =
            viewModel.getCategoryName(recipe.type) ?: getString(R.string.recipe_new_string3)

        if (viewModel.tempRecipe == null) {
            binding?.title?.setText(recipe.title)
            binding?.author?.setText(recipe.author)
            binding?.favoriteButton?.isChecked = recipe.isFavorite
            viewModel.tempRecipe = recipe.copy()
        } else {
            val tr = viewModel.tempRecipe
            binding?.title?.setText(tr?.title)
            binding?.author?.setText(tr?.author)
            binding?.favoriteButton?.isChecked = tr!!.isFavorite
            selectedSpinner =
                viewModel.getCategoryName(tr.type) ?: getString(R.string.recipe_new_string3)
        }

        val spinner = binding?.categoryChoose
        val arrAdapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)

        viewModel.catData.observe(viewLifecycleOwner) { catList ->
            arrAdapter.clear()
            arrAdapter.addAll(catList.map { it.name })

            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = arrAdapter
            spinner?.onItemSelectedListener = this

            val indx = arrAdapter.getPosition(selectedSpinner)
            spinner?.setSelection(indx)
        }

        val adapter = StepsAdapter(viewModel, StepsAdapter.EDIT_ADAPTER)
        binding?.stepsListNew?.adapter = adapter

        val rw = binding?.stepsListNew ?: return binding?.root
        adapter.attachRecyclerView(rw)

        viewModel.stepsAllData.observe(viewLifecycleOwner) { steps ->
            adapter.submitList(steps.filter { it.recipeId == recipe.id })
        }

        binding?.newStepButton?.setOnClickListener {
            viewModel.addNewEditedStep()
        }

        viewModel.navigateToNewStepEdit.observe(viewLifecycleOwner) {
            if (viewModel.getEditedStep() == null) return@observe
            findNavController().navigate(R.id.toStepNewFragment)
        }

        binding?.author?.doOnTextChanged { text, _, _, _ ->
            val selectedSpinner = viewModel.selectedSpinner
            val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
            viewModel.tempRecipe =
                viewModel.tempRecipe?.copy(author = text.toString(), type = catId) ?: Recipe(
                    NEW_ITEM_ID,
                    author = text.toString(),
                    title = "",
                    type = catId,
                    isFavorite = false
                )
        }
        binding?.title?.doOnTextChanged { text, _, _, _ ->
            val selectedSpinner = viewModel.selectedSpinner
            val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
            viewModel.tempRecipe =
                viewModel.tempRecipe?.copy(title = text.toString(), type = catId) ?: Recipe(
                    NEW_ITEM_ID,
                    title = text.toString(),
                    author = "",
                    type = catId,
                    isFavorite = false
                )
        }
        binding?.favoriteButton?.setOnClickListener {
            val selectedSpinner = viewModel.selectedSpinner
            val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
            val button = it as MaterialButton
            viewModel.tempRecipe =
                viewModel.tempRecipe?.copy(isFavorite = button.isChecked, type = catId)
                    ?: Recipe(
                        NEW_ITEM_ID,
                        title = "",
                        author = "",
                        type = catId,
                        isFavorite = button.isChecked
                    )
        }

        return binding?.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        viewModel.selectedSpinner = parent?.getItemAtPosition(pos) as String
        val selectedSpinner = viewModel.selectedSpinner
        val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
        viewModel.tempRecipe = viewModel.tempRecipe?.copy(type = catId) ?: Recipe(
            NEW_ITEM_ID,
            title = "",
            author = "",
            type = catId,
            isFavorite = false
        )
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun goBackWithDialog() {
        val nameIsSame =
            viewModel.getEditedRecipe()?.title?.equals(binding?.title?.text.toString()) ?: false
        val authorIsSame =
            viewModel.getEditedRecipe()?.author?.equals(binding?.title?.text.toString())
                ?: false
        val selectedSpinner = viewModel.selectedSpinner
        val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
        val catChanged = viewModel.getEditedRecipe()?.type != catId
        val recipeDiscard = viewModel.getEditedRecipe()

        if (!nameIsSame || !authorIsSame || catChanged) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.recipe_new_string6))
                .setNegativeButton(getString(R.string.recipe_new_string7)) { dialog, which ->

                }.setPositiveButton(getString(R.string.recipe_new_string8)) { dialog, which ->
                    if (viewModel.isNewRecipe && recipeDiscard != null) viewModel.deleteRecipe(
                        recipeDiscard
                    )
                    viewModel.tempRecipe = null
                    findNavController().popBackStack()
                }.show()
        } else {
            if (viewModel.isNewRecipe && recipeDiscard != null) viewModel.deleteRecipe(recipeDiscard)
            viewModel.tempRecipe = null
            findNavController().popBackStack()
        }
    }
    companion object {
        const val REQUEST_KEY = ".edit fragment"
        const val RESULT_KEY = ".EDIT RESULT"
        const val RESULT_VALUE = ".EDITED OK"
    }
}