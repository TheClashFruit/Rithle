package me.theclashfruit.rithle

import android.accounts.AccountManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.databinding.ActivityMainBinding
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.facets.Category
import me.theclashfruit.rithle.modrinth.facets.FacetBuilder
import me.theclashfruit.rithle.modrinth.facets.ProjectType
import me.theclashfruit.rithle.onboarding.OnboardingActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var searchBar: SearchBar;
    private lateinit var searchView: SearchView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        searchBar = binding.searchBar;
        searchView = binding.searchView;

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
        }

        val auth = AccountManager.get(this).getAuthToken(AccountManager.get(this).getAccountsByType("me.theclashfruit.rithle")[0], "pat", null, this, null, null)

        val modrinthApi = Modrinth.getInstance(this)

        lifecycleScope.launch(Dispatchers.Default){
            val token = auth.result.getString(AccountManager.KEY_AUTHTOKEN) ?: "unset"

            if (token != "unset") {
                modrinthApi.setToken(token)
            } else {
                Intent(this@MainActivity, OnboardingActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }

            modrinthApi.loggedInUser {
                loadImage(it.avatarUrl)
            }
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

        searchBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    MaterialAlertDialogBuilder(this)
                        .setItems(arrayOf("Account", "Settings")) { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()

                    true
                }
                else -> false
            }
        }

        val listView = binding.listView

        val facets =
            FacetBuilder()
                .addCategories(Category.ALL_MOD_LOADERS)
                .addProjectType(ProjectType.MOD)
                .build()

        searchView
            .editText
            .setOnEditorActionListener { v, actionId, event ->
                Log.d("RithleApp", "Search: ${v.text}")

                searchBar.setText(searchView.text);
                searchView.hide();

                false
            }


        searchView
            .editText
            .addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    modrinthApi.search(searchView.editText.text.toString(), facets) {
                        val list = mutableListOf<String>()

                        it.forEach { project ->
                            list.add(project.title)
                        }

                        listView.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, list)

                        listView.refreshDrawableState()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {}
            })

        /*
        val sharedPref = getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)

        val token = sharedPref.getString("modrinth_token", "")

        if (token == null) {
            Intent(this, OnboardingActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        val modrinthApi = Modrinth.getInstance(this)

        modrinthApi.setToken(token!!)

        modrinthApi.loggedInUser {
            loadImage(it.avatarUrl)
        }

        val facets =
            FacetBuilder()
                .addCategories(Category.ALL_MOD_LOADERS)
                .addProjectType(ProjectType.MOD)
                .build()

        modrinthApi.search("", facets) {
            val nf = NumberFormat.getInstance(Locale.getDefault())

            it.forEach { project ->
                Log.d("RithleApp", "${project.title} ${nf.format(project.downloads)}")
            }
        }

        val listView = binding.listView

        searchView
            .editText
            .setOnEditorActionListener { v, actionId, event ->
                Log.d("RithleApp", "Search: ${v.text}")

                searchBar.setText(searchView.text);
                searchView.hide();

                false
            }

        val contextus = this

        searchView
            .editText
            .addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    modrinthApi.search(searchView.editText.text.toString(), facets) {
                        val list = mutableListOf<String>()

                        it.forEach { project ->
                            list.add(project.title)
                        }

                        listView.adapter = ArrayAdapter(contextus, android.R.layout.simple_list_item_1, list)

                        listView.refreshDrawableState()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        */
    }

    private fun loadImage(avatarUrl: String?) {
        try {
            Glide.with(this)
                .load(avatarUrl)
                .centerCrop()
                .circleCrop()
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("Glide", "loadImage: failed")
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("Glide", "loadImage: ready")
                        resource?.let { renderProfileImage(it) }
                        return true
                    }

                }).submit()
        } catch (e: Exception) {
            Log.e("GlideR", "loadImage: ${e.message}")
        }
    }

    private fun renderProfileImage(resource: Drawable) {
        lifecycleScope.launch(Dispatchers.Main){
            searchBar.menu.findItem(R.id.profile).icon = resource
        }
    }

    private fun registerNotificationChannel(id: String, name: String, description: String, importance: Int) {
        val notificationChannel = NotificationChannel(id, name, importance)

        notificationChannel.description = description

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notificationChannel)

    }
}