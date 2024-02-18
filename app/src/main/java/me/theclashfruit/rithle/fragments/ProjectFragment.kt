package me.theclashfruit.rithle.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.databinding.FragmentMainBinding
import me.theclashfruit.rithle.databinding.FragmentProjectBinding
import me.theclashfruit.rithle.modrinth.Modrinth

class ProjectFragment : Fragment() {
    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!

    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectId = it.getString("project_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProjectBinding.inflate(inflater, container, false)

        val modrinthApi = Modrinth.getInstance(requireContext())

        modrinthApi.project(projectId!!) {
            binding.textView.text = it.toString()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(projectId: String) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putString("project_id", projectId)
                }
            }
    }
}