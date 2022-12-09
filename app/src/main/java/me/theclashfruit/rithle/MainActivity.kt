package me.theclashfruit.rithle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.theclashfruit.rithle.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainFragmentTransaction = supportFragmentManager.beginTransaction()
        val homeFragment            = HomeFragment.newInstance()

        mainFragmentTransaction
            .replace(R.id.parentFragmentContainer, homeFragment)
            .commit()

        MaterialAlertDialogBuilder(this)
            .setTitle("⚠️ Warning!")
            .setMessage("This is an alpha build, it is not intended for regular use, report bugs on GitHub.")
            .setPositiveButton("Ok") { dialog, which ->

            }
            .show()
    }
}
