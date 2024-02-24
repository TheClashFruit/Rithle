package me.theclashfruit.rithle

import android.accounts.AccountManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.databinding.ActivityMainBinding
import me.theclashfruit.rithle.fragments.MainFragment
import me.theclashfruit.rithle.fragments.ProjectFragment
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.models.Project
import me.theclashfruit.rithle.onboarding.OnboardingActivity
import me.theclashfruit.rithle.service.NotificationService
import me.theclashfruit.rithle.util.ImageUtil


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var fragmentView: FragmentContainerView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fragmentView = binding.fragmentContainerView;

        val sharedPref = getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)

        registerNotificationChannel(
            "mr:project_update",
            getString(R.string.project_update),
            getString(R.string.project_update_desc),
            3
        )
        registerNotificationChannel(
            "mr:team_invite",
            getString(R.string.team_invite),
            getString(R.string.team_invite_desc),
            4
        )
        registerNotificationChannel(
            "mr:organization_invite",
            getString(R.string.organization_invite),
            getString(R.string.organization_invite_desc),
            4
        )
        registerNotificationChannel(
            "mr:status_change",
            getString(R.string.status_change),
            getString(R.string.status_change_desc),
            3
        )
        registerNotificationChannel(
            "mr:moderator_message",
            getString(R.string.moderator_message),
            getString(R.string.moderator_message_desc),
            5
        )

        if(AccountManager.get(this).getAccountsByType("me.theclashfruit.rithle").isEmpty()) {
            Intent(this, OnboardingActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else {
            val auth = AccountManager.get(this).getAuthToken(AccountManager.get(this).getAccountsByType("me.theclashfruit.rithle")[0], "pat", null, this, null, null)

            val modrinthApi = Modrinth.getInstance(this)

            lifecycleScope.launch(Dispatchers.Default) {
                val token = auth.result.getString(AccountManager.KEY_AUTHTOKEN) ?: "unset"

                if (token != "unset") {
                    modrinthApi.setToken(token)
                } else {
                    Intent(this@MainActivity, OnboardingActivity::class.java).also {
                        startActivity(it)
                        this@MainActivity.finish()
                    }
                }
            }
        }

        val mainFragment = MainFragment()

        if (intent?.action == Intent.ACTION_VIEW) {
            val data: Uri? = intent?.data

            if (data!!.path!!.matches(Regex("((/)((mod/.+)|(plugin/.+)|(datapack/.+)|(shader/.+)|(resourcepack/.+)|(modpack/.+)|(project/.+)))"))) {
                val id: String = data.pathSegments[1]

                supportFragmentManager
                    .beginTransaction()
                    .replace(fragmentView.id, ProjectFragment.newInstance(id))
                    .commit()
            }
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(fragmentView.id, mainFragment)
                .commit()
        }

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Notifications")
                        .setMessage("Rithle requires notification permissions in order to send you notifications.\n\nDismissing this dialog will dismiss it forever.")
                        .setNegativeButton("Dismiss") { _, _ ->
                            sharedPref
                                .edit()
                                .putBoolean("notifications_perms", false)
                                .apply()
                        }
                        .setPositiveButton("Change") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.setData(uri)
                            startActivity(intent)
                        }
                        .setOnDismissListener {
                            sharedPref
                                .edit()
                                .putBoolean("notifications_perms", false)
                                .apply()
                        }
                        .show()
                }
            }

        if(sharedPref.getBoolean("notifications_perms", true) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

        /*
        val intent = Intent(this, NotificationService::class.java)

        startService(intent)
        */
    }

    private fun registerNotificationChannel(id: String, name: String, description: String, importance: Int) {
        val notificationChannel = NotificationChannel(id, name, importance)

        notificationChannel.description = description

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notificationChannel)

    }
}