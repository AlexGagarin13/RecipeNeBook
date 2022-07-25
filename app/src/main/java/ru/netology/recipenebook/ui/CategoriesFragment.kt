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
import ru.netology.recipenebook.adapter.CategoriesAdapter
import ru.netology.recipenebook.databinding.CategoriesFragmentBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class CategoriesFragment: Fragment() {
    private val viewModel: RecipeViewModel by activityViewModels()
    private var _binding: CategoriesFragmentBinding? = null
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
        _binding = CategoriesFragmentBinding.inflate(inflater)

        val adapter = CategoriesAdapter(viewModel)
        binding?.categoriesList?.adapter = adapter

        viewModel.catData.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }

        binding?.filterOk?.setOnClickListener {
            findNavController().navigate(R.id.feedFragment)
        }
        return binding?.root
    }

}