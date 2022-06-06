package ru.netology.recipenebook.ui

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

        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }
    }.root

    private fun onOkButtonClicked(binding: UpdateRecipeFragmentBinding) {

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