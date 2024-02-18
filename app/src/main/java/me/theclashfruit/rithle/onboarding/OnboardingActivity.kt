package me.theclashfruit.rithle.onboarding

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        val accountManager = AccountManager.get(this)

        buttonContinue.setOnClickListener {
            val pat = patField.editText?.text.toString()

            if (pat.isNotEmpty()) {
                try {
                    val modrinthApi = Modrinth.getInstance(this)

                    modrinthApi.setToken(pat)

                    modrinthApi.loggedInUser {
                        val account = Account(it.username, "me.theclashfruit.rithle")
                        val userData = Bundle()

                        userData.putString("id", it.id)

                        accountManager.addAccountExplicitly(account, pat, userData)
                        accountManager.setAuthToken(account, "pat", pat)
                        accountManager.notifyAccountAuthenticated(account)

                        Intent(this, MainActivity::class.java).also { i ->
                            startActivity(i)
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