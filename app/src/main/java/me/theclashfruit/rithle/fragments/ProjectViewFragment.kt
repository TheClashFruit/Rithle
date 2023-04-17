package me.theclashfruit.rithle.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.MrApiUrlUtil
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthProjectModel

class ProjectViewFragment : Fragment() {
    private var modId: String? = null
    private var dataRaw: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            modId = it.getString("modId")
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_project_view, container, false)

        val toolBar: MaterialToolbar           = rootView.findViewById(R.id.toolbar)
        // val bottomNavBar: BottomNavigationView = rootView.findViewById(R.id.bottomNavigation)

        toolBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /*
        bottomNavBar.setOnNavigationItemSelectedListener { item ->
            val bottomNavFragmentTransaction = parentFragmentManager.beginTransaction()

            when(item.itemId) {
                R.id.itemInfo -> {
                    val infoFragment = ProjectInfoFragment.newInstance(dataRaw!!)

                    bottomNavFragmentTransaction
                        .replace(R.id.projectFragmentContainer, infoFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemChangelog -> {
                    val descriptionFragment = ProjectDescriptionFragment.newInstance(dataRaw!!)

                    bottomNavFragmentTransaction
                        .replace(R.id.projectFragmentContainer, descriptionFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemDownloads -> {
                    val downloadsFragment = ProjectDownloadsFragment.newInstance(modId)

                    bottomNavFragmentTransaction
                        .replace(R.id.projectFragmentContainer, downloadsFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemSettings -> {

                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
        */

        val format = Json { ignoreUnknownKeys = true }

        val jsonObjectRequest = @SuppressLint("SetTextI18n")
        object : JsonObjectRequest(
            Method.GET, MrApiUrlUtil().getApiUrl() + "/v2/project/${modId}", null,
            { response ->
                    dataRaw  = response.toString()
                val dataJson = format.decodeFromString<ModrinthProjectModel>(response.toString())

                toolBar.subtitle = dataJson.title

                val infoFragment = ProjectInfoFragment.newInstance(dataRaw!!)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.projectFragmentContainer, infoFragment)
                    .commit()
            },
            { error ->
                Log.e("webCall", error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/${BuildConfig.VERSION_NAME} (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Volley/1.2.1"
                return headers
            }
        }

        RithleSingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(modId: String) =
            ProjectViewFragment().apply {
                arguments = Bundle().apply {
                    putString("modId", modId)
                }
            }
    }
}