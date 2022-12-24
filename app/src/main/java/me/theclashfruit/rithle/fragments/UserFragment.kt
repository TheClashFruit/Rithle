package me.theclashfruit.rithle.fragments

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import com.android.volley.toolbox.JsonObjectRequest
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.RithleSingleton

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

        if(authToken == "") {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(requireContext(), Uri.parse("https://github.com/login/oauth/authorize?client_id=2f7fbf1e6e196b0d2069"))

            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commit()
        }

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "https://api.modrinth.com/v2/user", null,
            { response ->
                rootView.findViewById<TextView>(R.id.textView7).text =  "${response.getString("username")} [${response.getString("role").capitalize()}]"
                rootView.findViewById<TextView>(R.id.textView8).text =  "\$${response.getJSONObject("payout_data").getString("balance")} earned so far.."
                Log.e("webCall", response.toString())
            },
            { error ->
                Log.e("webCall", error.stackTraceToString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/${BuildConfig.VERSION_NAME} (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Volley/1.2.1"
                headers["Authorization"] = authToken.toString()
                return headers
            }
        }

        RithleSingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)

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