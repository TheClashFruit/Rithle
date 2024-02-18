package me.theclashfruit.rithle

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
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
import java.text.NumberFormat
import java.util.Locale

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

        val sharedPref = getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)

        val token = sharedPref.getString("modrinth_token", null)

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
}