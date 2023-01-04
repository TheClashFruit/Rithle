package me.theclashfruit.rithle.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.appbar.MaterialToolbar
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.MrApiUrlUtil
import me.theclashfruit.rithle.classes.RithleSingleton
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap

class UserFragment : Fragment() {
    private var userId: String? = null

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

            parentFragmentManager.popBackStack()
        }

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, MrApiUrlUtil().getApiUrl() + "/v2/user", null,
            { response ->
                val format: NumberFormat = NumberFormat.getCurrencyInstance()
                format.maximumFractionDigits = 0
                format.currency = Currency.getInstance("USD")
                format.maximumFractionDigits = 2

                rootView.findViewById<TextView>(R.id.textView7).text =  "${response.getString("username")} [${response.getString("role").capitalize()}]"
                rootView.findViewById<TextView>(R.id.textView8).text =  "${format.format(response.getJSONObject("payout_data").getString("balance").toFloat())} earned so far.."

                userId = response.getString("username")

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

        val toolBar: MaterialToolbar = rootView.findViewById(R.id.toolbar)

        toolBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        toolBar.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.toolbarEdit -> {
                    return@setOnMenuItemClickListener true
                }
                R.id.toolbarShare -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "https://modrinth.com/user/$userId")
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)

                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }

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