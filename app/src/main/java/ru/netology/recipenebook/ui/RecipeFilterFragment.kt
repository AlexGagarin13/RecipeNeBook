package ru.netology.recipenebook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.recipenebook.databinding.FilterFragmentBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class RecipeFilterFragment : Fragment() {
    private val viewModel by activityViewModels<RecipeViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FilterFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        binding.applyFilterBtn.setOnClickListener {
            onApplyBtnClicked(binding)
        }
    }.root


    private fun onApplyBtnClicked(binding: FilterFragmentBinding) {
        //checkedCount don't allow to turn off all filters (
        var checkedCount = 0
        val cuisineCount = 7

        if (!binding.checkBoxEuropean.isChecked) {
            ++checkedCount
            viewModel.showEuropean("European")
        }
        if (!binding.checkBoxAsian.isChecked) {
            ++checkedCount
            viewModel.showAsian("Asian")

        }
        if (!binding.checkBoxPanasian.isChecked) {
            ++checkedCount
            viewModel.showPanasian("Panasian")

        }
        if (!binding.checkBoxEastern.isChecked) {
            ++checkedCount
            viewModel.showEastern("Eastern")
        }
        if (!binding.checkBoxAmerican.isChecked) {
            ++checkedCount
            viewModel.showAmerican("American")
        }
        if (!binding.checkBoxRussian.isChecked) {
            ++checkedCount
            viewModel.showRussian("Russian")
        }
        if (!binding.checkBoxMediterranean.isChecked) {
            ++checkedCount
            viewModel.showMediterranean("Mediterranean")
        }

        if (checkedCount == cuisineCount) {
            Toast.makeText(activity, "All filters can't be disabled", Toast.LENGTH_LONG).show()
            return
        } else findNavController().popBackStack()
    }

}