package me.theclashfruit.rithle.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel
import org.json.JSONArray
import org.json.JSONObject

class DownloadsAdapter(private val downloadsList: JSONArray, private val appContext: Context, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<DownloadsAdapter.StreamHolder?>() {

    class StreamHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(itemView: View) {
            Log.d("RecyclerView", "CLICK!")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StreamHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view: View = layoutInflater.inflate(R.layout.list_downloads, parent, false)

        return StreamHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StreamHolder, position: Int) {
        val versionNumber: TextView = holder.itemView.findViewById(R.id.textView9)
        val versionCompatibility: TextView = holder.itemView.findViewById(R.id.textView10)

        val downloadButton: ImageView = holder.itemView.findViewById(R.id.imageView3)

        val data = downloadsList.getJSONObject(position)
        var versionList = ""

        for (i in 0 until data.getJSONArray("game_versions").length()) {
            versionList = if (i == 0)
                data.getJSONArray("game_versions").getString(i)
            else
                versionList + ", " + data.getJSONArray("game_versions").getString(i)
        }

        versionNumber.text        = data.getString("version_number")
        versionCompatibility.text = versionList

        downloadButton.setOnClickListener {
            CustomTabsIntent.Builder()
                .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
                .setColorSchemeParams(
                    CustomTabsIntent.COLOR_SCHEME_DARK,
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(
                            ContextCompat.getColor(
                                appContext,
                                R.color.colorPrimary
                            )
                        )
                        .build()
                )
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(
                            ContextCompat.getColor(
                                appContext,
                                R.color.colorPrimaryLight
                            )
                        )
                        .build()
                )
                .build()
                .launchUrl(appContext, Uri.parse(data.getJSONArray("files").getJSONObject(0).getString("url")))
        }
    }

    override fun getItemCount() = downloadsList.length()
}