package me.theclashfruit.rithle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.theclashfruit.rithle.R

class ProjectDownloadsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_downloads, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProjectDownloadsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}