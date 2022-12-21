package me.theclashfruit.rithle.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import me.theclashfruit.rithle.R

class SettingsContainerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings_container, container, false)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val settingsFragment    = SettingsFragment()

        fragmentTransaction
            .replace(R.id.settingsContainer, settingsFragment)
            .commit()

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SettingsContainerFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}