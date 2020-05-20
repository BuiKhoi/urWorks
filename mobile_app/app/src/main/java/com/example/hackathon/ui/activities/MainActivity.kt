package com.example.hackathon.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.*
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hackathon.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isOpenActionButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Full Screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
        resetDefaultFabs()
        // find button by id
        var bottom_nav: BottomNavigationView = findViewById(R.id.bottom_nav)
        var history_button: FloatingActionButton = findViewById(R.id.des_history)
        var swipe_button: FloatingActionButton = findViewById(R.id.des_swipe)
        var selected_button: FloatingActionButton = findViewById(R.id.des_selected)

        // Setup for bottom navigation
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottom_nav.setupWithNavController(navController)
        // Set navigate for fab button
        history_button.setOnClickListener {
            navController.navigate(R.id.des_history)
            resetDefaultFabs()
        }
        swipe_button.setOnClickListener {
            navController.navigate(R.id.des_swipe)
            resetDefaultFabs()
        }
        selected_button.setOnClickListener {
            navController.navigate(R.id.des_selected)
            resetDefaultFabs()
        }

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        // Set Action for floating button of bottom navigation
        action_menu_button.apply {
            // Set animation for all button fab when clicked action button
            this.setOnClickListener {
                isOpenActionButton = !isOpenActionButton
                if (isOpenActionButton) {
                    // Set animation translation for all button
                    history_button.animate().alpha(1f).translationX(-200f).translationY(-200f)
                        .setInterpolator(OvershootInterpolator())
                    swipe_button.animate().alpha(1f).translationY(-300f)
                        .setInterpolator(OvershootInterpolator()).setStartDelay(50)
                    selected_button.animate().alpha(1f).translationX(200f).translationY(-200f)
                        .setInterpolator(OvershootInterpolator()).setStartDelay(100)
                } else {
                    resetDefaultFabs()
                }
            }
        }


    }

    // Set animation translation default for all fabs button
    fun resetDefaultFabs(){
        isOpenActionButton = false
        des_history.animate().translationX(0f).translationY(0f)
            .setInterpolator(DecelerateInterpolator()).alpha(0f)
        des_swipe.animate().translationX(0f).translationY(0f)
            .setInterpolator(DecelerateInterpolator()).alpha(0f)
        des_selected.animate().translationX(0f).translationY(0f)
            .setInterpolator(DecelerateInterpolator()).alpha(0f)
    }
}
