package me.theclashfruit.rithle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.models.ModrinthProjectModel

class ProjectChangelogFragment : Fragment() {
    private var projectData: ModrinthProjectModel? = null
    private var modId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            modId = it.getString("modId")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_description, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(modId: String?) =
            ProjectInfoFragment().apply {
                arguments = Bundle().apply {
                    putString("modId", modId)
                }
            }
    }
}