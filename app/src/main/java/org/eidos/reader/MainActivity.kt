package org.eidos.reader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import org.eidos.reader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // finding the toolbar
        val toolbar = binding.toolbar
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        drawerLayout = binding.drawerLayout

        /*
        This code sets up the tool bar with the hamburger menus and shit

        Because the toolbar only appears in one place (aka the activity), the toolbar
        will be similar throughout the entire application.

        If we want to implement different toolbars for different fragments, we need to
        set up the tool bar from within each fragment. That way, the fragment can control
        the visuals of the app bar.

        non-functional code for reference:
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        See below for more information about implementing different tool bars:
        https://developer.android.com/guide/navigation/navigation-ui#support_app_bar_variations

        For implementing the drawer
        https://stackoverflow.com/questions/26440879/how-do-i-use-drawerlayout-to-display-over-the-actionbar-toolbar-and-under-the-st
        https://developer.android.com/guide/navigation/navigation-ui#add_a_navigation_drawer
         */
        NavigationUI.setupWithNavController(toolbar, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}