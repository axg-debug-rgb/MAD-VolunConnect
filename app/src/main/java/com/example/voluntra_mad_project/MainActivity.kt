package com.example.voluntra_mad_project

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback // <-- REQUIRED
import com.example.voluntra_mad_project.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton // <-- REQUIRED

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isMapVisible = false // State variable to track the current view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Initial Fragment Setup: Show the List by default
        if (savedInstanceState == null) {
            showFragment(EventListFragment(), addToBackStack = false)
        }

        // 2. Set up the Toggle FAB listener (R.id.fab_map_list_toggle)
        val fabToggle = findViewById<FloatingActionButton>(R.id.fab_map_list_toggle)
        fabToggle.setOnClickListener {
            if (isMapVisible) {
                // Currently showing Map, switch to List
                showFragment(EventListFragment(), addToBackStack = false)
            } else {
                // Currently showing List, switch to Map
                showFragment(MapOverlayFragment(), addToBackStack = true)
            }
            isMapVisible = !isMapVisible
            updateFabIcon(fabToggle)
        }

        // 3. CORRECT BACK PRESS LOGIC (Modern API)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    // Pop the Map Fragment back to the List Fragment
                    supportFragmentManager.popBackStack()

                    // Update state and FAB icon
                    isMapVisible = false
                    updateFabIcon(fabToggle)
                } else {
                    // Otherwise, let the system handle the back press (exit app)
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    // Helper function to handle fragment transactions
    private fun showFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        if (addToBackStack) {
            // Add to back stack when navigating from List to Map
            transaction.addToBackStack("map_view")
        } else {
            // Remove all map entries from the stack when returning to the list
            supportFragmentManager.popBackStack("map_view", androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        transaction.commit()
    }

    // Helper function to change the FAB icon
    private fun updateFabIcon(fab: FloatingActionButton) {
        // You MUST have R.drawable.ic_map and R.drawable.ic_list created!
        val iconResId = if (isMapVisible) R.drawable.ic_list else R.drawable.ic_map
        fab.setImageResource(iconResId)
        fab.contentDescription = if (isMapVisible) "Show Event List" else "Show Map"
    }


    // --- Menu Handling ---
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // The old 'override fun onBackPressed()' is REMOVED, replaced by the logic in onCreate.
}