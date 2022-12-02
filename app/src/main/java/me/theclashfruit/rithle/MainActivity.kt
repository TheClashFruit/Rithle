package me.theclashfruit.rithle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.theclashfruit.rithle.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainFragmentTransaction = supportFragmentManager.beginTransaction()
        val homeFragment            = HomeFragment.newInstance()

        mainFragmentTransaction
            .addToBackStack("homeFragment")
            .add(R.id.parentFragmentContainer, homeFragment)
            .commit()
    }
}
