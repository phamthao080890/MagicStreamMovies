package com.magicstreammovies.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magicstreammovies.R
import com.magicstreammovies.data.session.SessionManager
import com.magicstreammovies.ui.fragment.FavouritesFragment
import com.magicstreammovies.ui.fragment.GenresFragment
import com.magicstreammovies.ui.fragment.HomeFragment
import com.magicstreammovies.ui.fragment.LoginFragment
import com.magicstreammovies.ui.fragment.MovieDetailsFragment
import com.magicstreammovies.ui.fragment.ProfileFragment
import com.magicstreammovies.ui.fragment.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var tabBar: BottomNavigationView

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = getColor(R.color.nav_dark)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        tabBar = findViewById(R.id.tab_bar)
        refreshSessionNavigation()
        tabBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> showRoot(HomeFragment(), getString(R.string.movies))
                R.id.nav_genres -> showRoot(GenresFragment(), getString(R.string.genres))
                R.id.nav_favourites -> requireLogin(FavouritesFragment(), getString(R.string.favourites))
                R.id.nav_profile -> requireLogin(ProfileFragment(), getString(R.string.profile))
            }
            true
        }
        supportFragmentManager.addOnBackStackChangedListener {
            updateAppBarForCurrentScreen()
        }
        if (state == null) showRoot(HomeFragment(), getString(R.string.movies))
    }

    private fun requireLogin(fragment: Fragment, title: String) {
        if (SessionManager(this).isLoggedIn()) {
            showRoot(fragment, title)
        } else {
            val destination = when (fragment) {
                is ProfileFragment -> LoginFragment.DESTINATION_PROFILE
                is FavouritesFragment -> LoginFragment.DESTINATION_FAVOURITES
                else -> LoginFragment.DESTINATION_HOME
            }
            showRoot(LoginFragment.create(destination), getString(R.string.sign_in))
        }
    }

    fun showRoot(fragment: Fragment, title: String) {
        supportFragmentManager.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE)
        supportActionBar?.title = title
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateAppBarForCurrentScreen() {
        when (supportFragmentManager.findFragmentById(R.id.fragment_container)) {
            is HomeFragment -> supportActionBar?.title = getString(R.string.movies)
            is GenresFragment -> supportActionBar?.title = getString(R.string.genres)
            is FavouritesFragment -> supportActionBar?.title = getString(R.string.favourites)
            is ProfileFragment -> supportActionBar?.title = getString(R.string.profile)
            is LoginFragment -> supportActionBar?.title = getString(R.string.sign_in)
            is SearchFragment -> supportActionBar?.title = getString(R.string.search)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(
            supportFragmentManager.findFragmentById(R.id.fragment_container) is MovieDetailsFragment
        )
    }

    fun show(fragment: Fragment, title: String) {
        supportActionBar?.title = title
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            supportFragmentManager.popBackStack()
            true
        }

        R.id.action_search -> {
            show(SearchFragment(), getString(R.string.search))
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }

    fun refreshSessionNavigation() {
        tabBar.menu.findItem(R.id.nav_profile).title = getString(
            if (SessionManager(this).isLoggedIn()) R.string.profile else R.string.sign_in
        )
    }

    fun showAfterLogin(destination: String) {
        when (destination) {
            LoginFragment.DESTINATION_PROFILE -> {
                tabBar.menu.findItem(R.id.nav_profile).isChecked = true
                showRoot(ProfileFragment(), getString(R.string.profile))
            }
            LoginFragment.DESTINATION_FAVOURITES -> {
                tabBar.menu.findItem(R.id.nav_favourites).isChecked = true
                showRoot(FavouritesFragment(), getString(R.string.favourites))
            }
            else -> {
                tabBar.menu.findItem(R.id.nav_home).isChecked = true
                showRoot(HomeFragment(), getString(R.string.movies))
            }
        }
    }
}
