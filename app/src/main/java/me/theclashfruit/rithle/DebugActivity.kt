package me.theclashfruit.rithle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        MaterialAlertDialogBuilder(applicationContext)
            .setTitle("Looks like Rithle has crashed.")
            .setMessage(intent.getStringExtra("error"))
            .setPositiveButton("Ok") { dialog, which ->

            }
            .show()
    }
}