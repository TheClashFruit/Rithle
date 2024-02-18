package me.theclashfruit.rithle

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class LinkHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_handler)

        val action: String? = intent?.action
        val data: Uri? = intent?.data

        Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show()
    }
}