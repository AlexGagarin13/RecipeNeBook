package ru.netology.recipenebook.ui


class RecipeFavoriteShowFragment : Fragment() {

    private val viewModel by activityViewModels<RecipeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ShowFavoriteBinding.inflate(layoutInflater, container, false).also { binding ->

        viewModel.data.observe(viewLifecycleOwner) { recipes ->

            val favRecipes = recipes.filter { it.isFavorite }.isEmpty()
            if (favRecipes) {
                binding.emptyText.isVisible = favRecipes
                binding.emptyTextMessage.isVisible = favRecipes
                binding.emptyIcon.isVisible = favRecipes
            }
        }

        val adapter = PostsAdapter(viewModel)
        binding.favoriteList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val favRecipes = posts.filter { it.isFavorite }
            adapter.submitList(favRecipes)
        }

        binding.bottomNavBar.setOnItemSelectedListener { it ->
            when (it.itemId) {
                R.id.feed -> findNavController().popBackStack()
            }
            true
        }
    }.root
}