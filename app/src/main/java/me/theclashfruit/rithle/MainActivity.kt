package me.theclashfruit.rithle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import me.theclashfruit.rithle.fragments.ModsFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolBar: MaterialToolbar

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appBarLayout = findViewById(R.id.appBarLayout)
        toolBar      = findViewById(R.id.toolBar)

        navigationView = findViewById(R.id.navigationView)
        drawerLayout   = findViewById(R.id.drawerLayout)

        setSupportActionBar(toolBar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        supportActionBar!!.subtitle = "Mods"

        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.app_name, R.string.app_name)

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        val mainFragmentTransaction = supportFragmentManager.beginTransaction()

        val modsFragment = ModsFragment.newInstance()

        mainFragmentTransaction
            .replace(R.id.fragmentContainer, modsFragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerFragmentTransaction = supportFragmentManager.beginTransaction()

        when(item.itemId) {
            R.id.itemMods -> {
                val modsFragment = ModsFragment.newInstance()

                drawerFragmentTransaction
                    .replace(R.id.fragmentContainer, modsFragment)
                    .commit()

                return true
            }
        }

        return false
    }
}
