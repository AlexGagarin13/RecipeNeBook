package ru.netology.recipenebook.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.netology.recipenebook.R
import ru.netology.recipenebook.databinding.AppActivityBinding
import ru.netology.recipenebook.viewModel.RecipeViewModel

class AppActivity:AppCompatActivity() {
    private lateinit var navController: NavController
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AppActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        binding.bottomNavBar.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(ChangeTitlesAndSuppressUpButton())
    }

    inner class ChangeTitlesAndSuppressUpButton : NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {

            supportActionBar?.setDisplayShowHomeEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            supportActionBar?.title = when (destination.id) {
                R.id.feedFragment -> getString(R.string.app_name)
                R.id.favoriteShowFragment -> getString(R.string.favorites)
                R.id.categoriesFragment -> getString((R.string.cuisine_type_dummy))
                R.id.recipeShowCertainFragment -> getString(R.string.recipe_new_string2) + viewModel.showRecipe.value?.title
                R.id.recipeCreateFragment -> {
                    val recipe = viewModel.editRecipe.value ?: return
                    if (recipe.title.isBlank() || recipe.title.isEmpty())
                        getString(R.string.recipe_new_string4)
                    else
                        getString(R.string.recipe_new_string5) + recipe.title
                }
                R.id.stepNewFragment -> getString(R.string.nav_main_string4)
                else -> getString(R.string.app_name)
            }
        }
    }
}