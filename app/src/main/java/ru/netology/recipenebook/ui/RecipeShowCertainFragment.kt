package ru.netology.recipenebook.ui

class RecipeShowCertainFragment : Fragment() {
    private val args by navArgs<RecipeShowCertainFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ShowCertainRecipeBinding.inflate(layoutInflater, container, false).also { binding ->
        render(binding)
    }.root

    private fun render(binding: ShowCertainRecipeBinding) {
        binding.title.text = args.showRecipe?.title.toString()
        binding.author.text = args.showRecipe?.author.toString()
        binding.type.text = args.showRecipe?.type.toString()
        binding.content.text = args.showRecipe?.content.toString()
    }
}