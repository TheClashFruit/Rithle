package me.theclashfruit.rithle.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.noties.markwon.Markwon
import io.noties.markwon.image.glide.GlideImagesPlugin
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.databinding.FragmentProjectBinding
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.models.Project
import me.theclashfruit.rithle.util.ImageUtil


class ProjectFragment : Fragment() {
    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!

    private var projectId: String? = null
    private var markwon: Markwon? = null

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

        val topAppBar = binding.topAppBar

        markwon =
            Markwon
                .builder(requireActivity())
                .usePlugin(GlideImagesPlugin.create(ImageUtil(requireContext()).requestManager))
                .build()

        modrinthApi.loggedInUser { user ->
            modrinthApi.project(projectId!!) {
                markwon!!.setMarkdown(binding.textView, it.body)

                topAppBar.title = it.title
                topAppBar.subtitle = it.description

                projectData = it;
            }

            modrinthApi.projectMembers(projectId!!) {
                if(it.any { p -> p.user.id == user.id })
                    topAppBar.menu.findItem(R.id.action_edit).isVisible = true
            }

            modrinthApi.userFollowing(user) {
                if (it.any { p -> p.slug == projectId || p.id == projectId }) {
                    topAppBar.menu.findItem(R.id.action_follow).isVisible = false
                    topAppBar.menu.findItem(R.id.action_unfollow).isVisible = true
                } else {
                    topAppBar.menu.findItem(R.id.action_follow).isVisible = true
                    topAppBar.menu.findItem(R.id.action_unfollow).isVisible = false
                }
            }
        }

        topAppBar.setNavigationOnClickListener {
            requireActivity()
                .onBackPressed()
        }

        topAppBar.setOnMenuItemClickListener { item ->
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
                            topAppBar.menu.findItem(R.id.action_follow).isVisible = false
                            topAppBar.menu.findItem(R.id.action_unfollow).isVisible = true
                        }
                    }

                    true
                }
                R.id.action_unfollow -> {
                    modrinthApi.unfollowProject(projectData!!.id) {
                        if (it) {
                            topAppBar.menu.findItem(R.id.action_follow).isVisible = true
                            topAppBar.menu.findItem(R.id.action_unfollow).isVisible = false
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