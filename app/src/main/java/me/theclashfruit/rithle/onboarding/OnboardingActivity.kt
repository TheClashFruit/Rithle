package me.theclashfruit.rithle.onboarding

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.theclashfruit.rithle.MainActivity
import me.theclashfruit.rithle.databinding.ActivityOnboardingBinding
import me.theclashfruit.rithle.modrinth.Modrinth

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val patField = binding.patField
        val buttonContinue = binding.buttonContinue

        val sharedPref = getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)

        buttonContinue.setOnClickListener {
            val pat = patField.editText?.text.toString()

            if (pat.isNotEmpty()) {
                try {
                    val modrinthApi = Modrinth.getInstance(this)

                    modrinthApi.setToken(pat)

                    modrinthApi.loggedInUser {
                        sharedPref
                            .edit()
                            .putString("modrinth_token", pat)
                            .apply()

                        Intent(this, MainActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                } catch (e: IllegalStateException) {
                    patField.error = "Invalid PAT"
                }
            } else {
                patField.error = "PAT cannot be empty"
            }
        }
    }
}