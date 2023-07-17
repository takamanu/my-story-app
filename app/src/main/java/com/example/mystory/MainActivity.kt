package com.example.mystory

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.mystory.auth.AuthActivity
import com.example.mystory.databinding.ActivityMainBinding
import com.example.mystory.datamodel.LoginResponse
import com.example.mystory.vm.StoriesViewModel
import com.example.mystory.vm.ViewModelFactory
import com.google.gson.Gson
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionPreferences: SharedPreferences
//    private lateinit var networkConnectivityWatcher: NetworkConnectivityWatcher
    private lateinit var mainViewModel: MainViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var storiesViewModel: StoriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory(applicationContext)
        val viewModelProvider = ViewModelProvider(this, viewModelFactory)

        storiesViewModel = viewModelProvider[StoriesViewModel::class.java]

        storiesViewModel.story.observe(this) { data ->
            if (data != null){
                storyAdapter.submitData(lifecycle, data)
            }
        }

        sessionPreferences = getSharedPreferences("session", MODE_PRIVATE)


        if (!isSessionActive()) {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        sessionPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)
        val loginResponseJson = sessionPreferences.getString("loginResponse", null)
        val gson = Gson()

        val loginResponse = gson.fromJson(loginResponseJson, LoginResponse::class.java)

        val calendar = Calendar.getInstance()

        binding.floatingActionButton3.setOnClickListener {
            val moveToAddStoryActivity = Intent(this, AddStoryActivity::class.java)
            startActivity(moveToAddStoryActivity)
            finish()
        }

        storyAdapter = StoryAdapter()

        binding.apply {
            lifecycleScope.launchWhenCreated {
                loginResponse.loginResult?.token?.let { token ->
                    mainViewModel.showThisListStory(token).collect { storyList ->
                        Log.d("MainActivity", "This is the storyList $storyList")
//                        storyAdapter.submitData(storyList)
                    }

                }
            }

            rvFind.layoutManager = LinearLayoutManager(rvFind.context)
            rvFind.setHasFixedSize(true)
            rvFind.adapter = storyAdapter

        }



        mainViewModel.isLoading.observe(this) {
            binding?.apply {
                if (it) {
                    progressBar2.visibility = View.VISIBLE
                    rvFind.visibility = View.INVISIBLE
                } else {
                    progressBar2.visibility = View.GONE
                    rvFind.visibility = View.VISIBLE
                }
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            R.id.action_maps -> {
                val toMapsActivity = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(toMapsActivity)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        networkConnectivityWatcher.stopWatchingConnectivity()
    }

    private fun showLogoutDialog() {
        MaterialDialog(this).show {
            customView(R.layout.dialog_logout) // Replace with your custom dialog layout

            // Handle dialog buttons
            positiveButton(R.string.logout) {
                sessionPreferences.edit().remove("loginResponse").apply()
                navigateToAuthActivity()
            }
            negativeButton(android.R.string.cancel)
        }
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isSessionActive(): Boolean {
        val loginResponseJson = sessionPreferences.getString("loginResponse", null)
        return loginResponseJson != null
    }


    companion object {
        const val EXTRA_USER = "extra_user"
    }

}

