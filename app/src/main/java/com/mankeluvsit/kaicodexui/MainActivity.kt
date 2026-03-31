package com.mankeluvsit.kaicodexui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import com.mankeluvsit.kaicodexui.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    binding.appBarMain.contentText.text = getString(R.string.home_content)
                }

                R.id.nav_codex_ui -> {
                    startActivity(Intent(this, CodexUiActivity::class.java))
                }
            }

            binding.drawerLayout.closeDrawers()
            true
        }

        binding.navView.setCheckedItem(R.id.nav_home)
        binding.appBarMain.contentText.text = getString(R.string.home_content)
    }
}
