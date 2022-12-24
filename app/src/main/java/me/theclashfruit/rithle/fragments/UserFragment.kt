package me.theclashfruit.rithle.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import me.theclashfruit.rithle.R

class UserFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)

        val sharedPref = activity?.getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)

        val authToken = sharedPref!!.getString("authToken", "")

        if(authToken == "")
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(requireContext(), Uri.parse("https://github.com/login/oauth/authorize?client_id=2f7fbf1e6e196b0d2069"))

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            UserFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}