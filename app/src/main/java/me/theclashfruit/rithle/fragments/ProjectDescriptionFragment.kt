package me.theclashfruit.rithle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.models.ModrinthProjectModel
import me.theclashfruit.rithle.models.ModrinthSearchModel
import org.json.JSONArray
import org.json.JSONObject

class ProjectDescriptionFragment : Fragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_description, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(projectDataString: String) =
            ProjectDescriptionFragment().apply {
                arguments = Bundle().apply {
                    putString("projectData", projectDataString)
                }
            }
    }
}