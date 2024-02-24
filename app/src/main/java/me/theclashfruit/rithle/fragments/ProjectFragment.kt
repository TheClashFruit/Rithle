package me.theclashfruit.rithle.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.databinding.FragmentMainBinding
import me.theclashfruit.rithle.databinding.FragmentProjectBinding
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.models.Project

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

        var projectData: Project? = null;

        modrinthApi.loggedInUser { user ->
            modrinthApi.project(projectId!!) {
                binding.textView.text = it.toString()

                binding.topAppBar.title = it.title
                binding.topAppBar.subtitle = it.description

                projectData = it;
            }

            modrinthApi.userFollowing(user) {
                if (it.any { p -> p.slug == projectId || p.id == projectId }) {
                    binding.topAppBar.menu.findItem(R.id.action_follow).isVisible = false
                    binding.topAppBar.menu.findItem(R.id.action_unfollow).isVisible = true
                } else {
                    binding.topAppBar.menu.findItem(R.id.action_follow).isVisible = true
                    binding.topAppBar.menu.findItem(R.id.action_unfollow).isVisible = false
                }
            }
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share -> {
                    val shareIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND

                        putExtra(Intent.EXTRA_SUBJECT, projectData!!.title)
                        putExtra(Intent.EXTRA_TEXT, "https://modrinth.com/project/${projectData!!.slug}")

                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        data = Uri.parse("https://modrinth.com/project/${projectData!!.slug}")
                    }

                    startActivity(Intent.createChooser(shareIntent, "Share Modrinth Project"))

                    true
                }
                R.id.action_follow -> {
                    modrinthApi.followProject(projectData!!.id) {
                        if (it) {
                            binding.topAppBar.menu.findItem(R.id.action_follow).isVisible = false
                            binding.topAppBar.menu.findItem(R.id.action_unfollow).isVisible = true
                        }
                    }

                    true
                }
                R.id.action_unfollow -> {
                    modrinthApi.unfollowProject(projectData!!.id) {
                        if (it) {
                            binding.topAppBar.menu.findItem(R.id.action_follow).isVisible = true
                            binding.topAppBar.menu.findItem(R.id.action_unfollow).isVisible = false
                        }
                    }

                    true
                }
                else -> false
            }
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