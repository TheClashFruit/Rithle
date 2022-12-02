package me.theclashfruit.rithle.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import io.noties.markwon.Markwon
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthProjectModel
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel
import me.theclashfruit.rithle.models.ModrinthSearchModel

class ProjectViewFragment : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_project_view, container, false)

        val textViewTitle: TextView       = rootView.findViewById(R.id.textViewTitle)
        val textViewAuthor: TextView      = rootView.findViewById(R.id.textViewBy)
        val textViewDescription: TextView = rootView.findViewById(R.id.textViewDescription)

        val imageViewIcon: ImageView      = rootView.findViewById(R.id.projectIcon)

        val format = Json { ignoreUnknownKeys = true }

        val markwon = Markwon.create(requireContext());

        val jsonObjectRequest = @SuppressLint("SetTextI18n")
        object : JsonObjectRequest(
            Method.GET, "https://api.modrinth.com/v2/project/${modId}", null,
            { response ->
                val data = format.decodeFromString<ModrinthProjectModel>(response.toString())

                textViewTitle.text  = data.title
                textViewAuthor.text = "By ${data.team}"

                markwon.setMarkdown(textViewDescription, data.body.toString())

                RithleSingleton.getInstance(requireContext()).imageLoader.get(data.icon_url, object : ImageLoader.ImageListener {
                    override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {
                        if (response != null) {
                            imageViewIcon.setImageBitmap(response.bitmap)
                        }
                    }

                    override fun onErrorResponse(error: VolleyError?) {
                        Log.d("imageLoader", "wtf are you doing, you either don't have internet or the url is fucking wrong, btw the error is: ${error.toString()}")
                    }
                })
            },
            { error ->
                // TODO: Handle error
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/0.0.1 (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Fuel/2.3.1"
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