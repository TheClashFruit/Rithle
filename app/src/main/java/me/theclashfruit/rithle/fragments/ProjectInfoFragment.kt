package me.theclashfruit.rithle.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthProjectModel
import java.text.NumberFormat
import java.util.*


class ProjectInfoFragment : Fragment() {
    private var projectData: ModrinthProjectModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val format = Json { ignoreUnknownKeys = true }

        arguments?.let {
            projectData = format.decodeFromString<ModrinthProjectModel>(it.getString("projectData")!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_project_info, container, false)

        val projectIcon = rootView.findViewById<ImageView>(R.id.imageView)

        val nf = NumberFormat.getInstance(Locale.UK)

        val textViewTitle     = rootView.findViewById<TextView>(R.id.textViewTitle)
        val textViewDownloads = rootView.findViewById<TextView>(R.id.textViewDownloads)
        val textViewFollowers = rootView.findViewById<TextView>(R.id.textViewFollowers)

        textViewTitle.text     = projectData!!.title
        textViewDownloads.text = resources.getQuantityString(R.plurals.project_downloads, projectData!!.downloads!!.toInt(), nf.format(projectData!!.downloads!!.toLong()).toString())
        textViewFollowers.text = resources.getQuantityString(R.plurals.project_followers, projectData!!.followers!!.toInt(), nf.format(projectData!!.followers!!.toLong()).toString())

        val markwon = Markwon.builder(requireContext())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(TablePlugin.create(requireContext()))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(ImagesPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver { view, link ->
                        view.callOnClick()

                        Log.w("UriLog", view.paddingTop.toString())
                        Log.w("UriLog", link)

                        val customTabsIntent = CustomTabsIntent.Builder().build()
                        customTabsIntent.launchUrl(requireContext(), Uri.parse(link))
                    }
                }
            })
            .build()


        /*
        String url = "https://google.com/";
CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
CustomTabsIntent customTabsIntent = builder.build();
customTabsIntent.launchUrl(this, Uri.parse(url));
         */

        markwon.setParsedMarkdown(rootView.findViewById(R.id.textViewDescription), markwon.render(markwon.parse(projectData!!.body!!)))

        if(projectData!!.icon_url != null) {
            RithleSingleton.getInstance(requireContext()).imageLoader.get(projectData!!.icon_url.toString(), object : ImageLoader.ImageListener {
                override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {
                    if (response != null) {
                        projectIcon.setImageBitmap(response.bitmap)
                    }
                }

                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("imageLoader", "wtf are you doing, you either don't have internet or the url is fucking wrong, btw the error is: ${error.toString()}")
                }
            })
        }

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(projectDataString: String) =
            ProjectInfoFragment().apply {
                arguments = Bundle().apply {
                    putString("projectData", projectDataString)
                }
            }
    }
}