package ru.netology.recipenebook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.recipenebook.R
import ru.netology.recipenebook.data.RecipeCategory
import ru.netology.recipenebook.databinding.CategoryDetailCheckboxBinding
import ru.netology.recipenebook.viewModel.CategoriesHelper

class CategoriesAdapter(private val helper: CategoriesHelper) :
    ListAdapter<RecipeCategory, CategoriesAdapter.CatViewHolder>(CategoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryDetailCheckboxBinding.inflate(inflater, parent, false)

        return CatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CatViewHolder(private val binding: CategoryDetailCheckboxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecipeCategory?) {
            if (item == null) return
            with(binding) {
                checkboxCategory.text = item.name
                checkboxCategory.isChecked = item.showOrNot

                checkboxCategory.tag = TAG_PROCESS
                checkboxCategory.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                    if (buttonView == null) return@setOnCheckedChangeListener
                    if (!buttonView.isPressed) return@setOnCheckedChangeListener

//                    if (checkboxCategory.tag == TAG_SKIP) { // we set the value with isChecked, skip the trigger
//                        checkboxCategory.tag = TAG_PROCESS
//                        return@setOnCheckedChangeListener
//                    }

                    if (isChecked) { // If user selected the category
                        helper.setCategoryVisible(item.id)
                        checkboxCategory.tag = TAG_PROCESS
                        return@setOnCheckedChangeListener
                    }

                    val num = helper.getNumberOfSelectedCategories()
                    if (num > 1) { // If user de-selected the category and we have enough, 2 and more
                        helper.setCategoryInvisible(item.id)
                        checkboxCategory.tag = TAG_PROCESS
                        return@setOnCheckedChangeListener
                    }

                    // now we have number of selected categories not more then 1, so
                    // we keep the checkbox set
                    // it doesn't trigger the CheckChanged listener... looks like
                    checkboxCategory.tag = TAG_SKIP
                    buttonView.isChecked = true
                    Toast.makeText(
                        binding.root.context,
                        binding.root.context.getString(R.string.cat_adapter_string1),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    private object CategoryDiffCallback : DiffUtil.ItemCallback<RecipeCategory>() {
        override fun areItemsTheSame(oldItem: RecipeCategory, newItem: RecipeCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecipeCategory, newItem: RecipeCategory): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val TAG_SKIP = ".SKIP"
        const val TAG_PROCESS = ".PROCESS"
    }

}