package me.theclashfruit.rithle.fragments

import android.media.Image
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthProjectModel

class ProjectInfoFragment : Fragment() {
    private var projectData: ModrinthProjectModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val format = Json { ignoreUnknownKeys = true }

        arguments?.let {
            projectData = format.decodeFromString<ModrinthProjectModel>(it.getString("projectData")!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_project_info, container, false)

        val projectIcon = rootView.findViewById<ImageView>(R.id.imageView)

        val textViewTitle     = rootView.findViewById<TextView>(R.id.textViewTitle)
        val textViewDownloads = rootView.findViewById<TextView>(R.id.textViewDownloads)
        val textViewFollowers = rootView.findViewById<TextView>(R.id.textViewFollowers)

        textViewTitle.text     = projectData!!.title
        textViewDownloads.text = "${projectData!!.downloads} Downloads"
        textViewFollowers.text = "${projectData!!.followers} Followers"

        RithleSingleton.getInstance(requireContext()).imageLoader.get(projectData!!.icon_url.toString(), object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {
                if (response != null) {
                    projectIcon.setImageBitmap(response.bitmap)
                }
            }

            override fun onErrorResponse(error: VolleyError?) {
                Log.d("imageLoader", "wtf are you doing, you either don't have internet or the url is fucking wrong, btw the error is: ${error.toString()}")
            }
        })

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(projectDataString: String) =
            ProjectInfoFragment().apply {
                arguments = Bundle().apply {
                    putString("projectData", projectDataString)
                }
            }
    }
}