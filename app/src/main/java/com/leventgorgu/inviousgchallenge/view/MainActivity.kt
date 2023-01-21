package com.leventgorgu.inviousgchallenge.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.leventgorgu.inviousgchallenge.R

class MainActivity : AppCompatActivity() {

    private lateinit var navigationController: NavController
    private lateinit var options : NavOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        options = NavOptions.Builder()
            .setEnterAnim(R.anim.from_detail)
            .build()

        navigationController = Navigation.findNavController(this, R.id.fragmentContainerView)
        NavigationUI.setupActionBarWithNavController(this, navigationController, AppBarConfiguration(navigationController.graph))
        navigationController.navigate(R.id.feedFragment,null,options)
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navigationController, AppBarConfiguration(navigationController.graph))
    }
}