package me.theclashfruit.rithle

import android.content.Intent
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

        val mIntent = Intent(this, MainActivity::class.java)

        if (data.toString().startsWith("rithle://auth")) {
            startActivity(mIntent)
            finish()
        } else if (data!!.path!!.matches(Regex("((/)((mod/.+)|(plugin/.+)|(datapack/.+)|(shader/.+)|(resourcepack/.+)|(modpack/.+)|(project/.+)))"))) {
            val projectId = data.pathSegments[1]

            mIntent.putExtra("id", projectId)

            mIntent.action = Intent.ACTION_VIEW

            startActivity(mIntent)
            finish()
        } else {
            throw IllegalArgumentException("Gay Horses (${data.path!!})")
        }
    }
}