package me.theclashfruit.rithle.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.serialization.decodeFromString
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.adapters.DownloadsAdapter
import me.theclashfruit.rithle.classes.MrApiUrlUtil
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthProjectModel

class ProjectDownloadsFragment : Fragment() {
    private var modId: String? = null

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
        val rootView = inflater.inflate(R.layout.fragment_project_downloads, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)

        val jsonObjectRequest = @SuppressLint("SetTextI18n")
        object : JsonArrayRequest(
            Method.GET, MrApiUrlUtil().getApiUrl() + "/v2/project/${modId}/version", null,
            { response ->
                val listAdapter = DownloadsAdapter(response, requireContext(), parentFragmentManager)
                val layoutManager = LinearLayoutManager(requireContext())

                recyclerView.layoutManager = layoutManager
                recyclerView.adapter       = listAdapter
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
        fun newInstance(modId: String?) =
            ProjectDownloadsFragment().apply {
                arguments = Bundle().apply {
                    putString("modId", modId)
                }
            }
    }
}