package com.example.zavod.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.zavod.R
import com.example.zavod.databinding.ActivityMainBinding
import com.example.zavod.model.Equipment
import com.example.zavod.repository.EquipmentRepository
import com.example.zavod.viewmodel.MainViewModel
import com.example.zavod.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel
    private var toolbarTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("token", null)

        if (token == null) {
            openLoginScreen()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                EquipmentRepository(applicationContext)
            )
        ).get(MainViewModel::class.java)

        observeViewModel()
        setupToolbar()
        setupNavigation()
    }

    private fun openLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbarTitle = layoutInflater
            .inflate(R.layout.toolbar_label, binding.toolbar, false) as TextView

        binding.toolbar.addView(toolbarTitle)
    }

    private fun setupNavigation() {
        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment_activity_main
        )

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_dashboard
        ).build()

        NavigationUI.setupActionBarWithNavController(
            this,
            navController,
            appBarConfiguration
        )

        NavigationUI.setupWithNavController(
            binding.navView,
            navController
        )

        binding.navView.setOnItemSelectedListener { item ->
            val id = item.itemId

            when (id) {
                R.id.navigation_out -> {
                    logout()
                    true
                }

                R.id.navigation_notifications -> {
                    openDailyCheck()
                    true
                }

                else -> NavigationUI.onNavDestinationSelected(
                    item,
                    navController
                )
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val id = destination.id

            if (destination.label != null) {
                toolbarTitle?.text = destination.label
            }

            if (id == R.id.profileFragment || id == R.id.navigation_notifications) {
                binding.navView.visibility = View.GONE
                supportActionBar?.hide()
            } else {
                binding.navView.visibility = View.VISIBLE
                supportActionBar?.show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.dailyEquipment.observe(this) { equipment ->
            openStartScreen(equipment)
        }

        viewModel.message.observe(this) { message ->
            toast(message)
        }
    }

    private fun openDailyCheck() {
        val passId = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("pass_id", "")

        viewModel.loadDailyEquipment(passId)
    }

    private fun openStartScreen(equipment: Equipment?) {
        if (equipment == null) {
            return
        }

        val intent = Intent(this, StartActivity::class.java).apply {
            putExtra("equipment_id", equipment.id ?: 0)
            putExtra("equipment_name", equipment.name)
            putExtra("inspection_type", "daily")
        }

        startActivity(intent)
    }

    private fun logout() {
        getSharedPreferences("auth", MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        openLoginScreen()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_profile) {
            navController.navigate(R.id.profileFragment)
            return true
        }

        if (item.itemId == android.R.id.home) {
            return navController.navigateUp() || super.onOptionsItemSelected(item)
        }

        return NavigationUI.onNavDestinationSelected(item, navController) ||
                super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun toast(message: String?) {
        if (message.isNullOrBlank()) {
            return
        }

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}