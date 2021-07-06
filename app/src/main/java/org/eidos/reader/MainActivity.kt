package org.eidos.reader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.eidos.reader.container.AppContainer
import org.eidos.reader.databinding.ActivityMainBinding
import org.eidos.reader.repository.EidosRepository
import org.eidos.reader.ui.misc.utilities.setupWithNavController
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentNavController: LiveData<NavController>? = null

    private lateinit var repository: EidosRepository
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = (application as EidosApplication).appContainer.repository

        viewModel.useNightMode.observe(this) { shouldUseNightMode ->
            if (shouldUseNightMode) {
                Timber.i("night mode activated")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                Timber.i("night mode deactivated")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // We will use Google's implementation of multiple backstack using
        // fragment attachment/detachment. This should be enough for a rudimentary app
        // with acceptable UX tradeoffs - i.e. navigating to another tab and back using the back
        // button is not possible iirc unless we implement it.
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = binding.navigationBar

        // FIXME: remember to update this line with the list of navigation graphs!!!
        val navGraphIds = listOf(
            R.navigation.browse,
            R.navigation.library,
            R.navigation.read,
            R.navigation.more
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
//        controller.observe(this, Observer { navController ->
//            setupActionBarWithNavController(navController)
//        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.i("Navigate Up Initiated, check if succeeded?")
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onNewIntent(intent: Intent?) {
        // TODO: handle search intent here
        super.onNewIntent(intent)
    }
}