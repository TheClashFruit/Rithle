package me.theclashfruit.rithle

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.theclashfruit.rithle.fragments.HomeFragment
import me.theclashfruit.rithle.services.NotificationService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainFragmentTransaction = supportFragmentManager.beginTransaction()
        val homeFragment            = HomeFragment.newInstance()

        val sharedPref = getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)
        val authToken  = sharedPref!!.getString("authToken", "")

        /*
        if(authToken != "")
            ContextCompat.startForegroundService(this, Intent(this, NotificationService::class.java))
        */

        mainFragmentTransaction
            .replace(R.id.parentFragmentContainer, homeFragment)
            .commit()

        MaterialAlertDialogBuilder(this)
            .setTitle("⚠️ Warning!")
            .setMessage("This is an alpha build, it is not intended for regular use, report bugs on GitHub.")
            .setPositiveButton("Ok") { dialog, which ->

            }
            .show()

        // https://github.com/login/oauth/authorize?client_id=2f7fbf1e6e196b0d2069
    }
}
