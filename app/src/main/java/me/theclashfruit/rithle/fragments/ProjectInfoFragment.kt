package me.theclashfruit.rithle.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.tabs.TabLayoutMediator
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImageItem
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.MrApiUrlUtil
import me.theclashfruit.rithle.classes.ProxySchemeHandler
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthProjectModel
import java.net.URL
import java.text.NumberFormat
import java.util.*


class ProjectInfoFragment : Fragment() {
    private var projectData: ModrinthProjectModel? = null
    private var modId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            modId = it.getString("modId")!!
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

        val markwon = Markwon.builder(requireContext())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(TablePlugin.create(requireContext()))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(ImagesPlugin.create { plugin ->
                // for example to return a drawable resource
                plugin.addSchemeHandler(object : ProxySchemeHandler() {
                    override fun handle(raw: String, uri: Uri): ImageItem {
                        val sharedPref = activity!!.getSharedPreferences("me.theclashfruit.rithle_preferences", Context.MODE_PRIVATE)
                        val doProxy    = sharedPref!!.getBoolean("imageProxy", false)

                        val url: URL =
                            if(doProxy)
                                URL("https://rthl.theclashfruit.me/img.php?url=$uri")
                            else
                                URL(uri.toString())

                        Log.d("ThatUrl", url.toString())

                        val image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        return ImageItem.withResult(BitmapDrawable(resources, image))
                    }

                    override fun supportedSchemes(): Collection<String?> {
                        return listOf("http", "https")
                    }
                })
            })
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver { view, link ->
                        view.callOnClick()

                        Log.w("UriLog", view.paddingTop.toString())
                        Log.w("UriLog", link)

                        CustomTabsIntent.Builder()
                            .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
                            .setColorSchemeParams(
                                CustomTabsIntent.COLOR_SCHEME_DARK,
                                CustomTabColorSchemeParams.Builder()
                                    .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                                    .build()
                            )
                            .setDefaultColorSchemeParams(
                                CustomTabColorSchemeParams.Builder()
                                    .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryLight))
                                    .build()
                            )
                            .build()
                            .launchUrl(requireContext(), Uri.parse(link))
                    }
                }
            })
            .build()

        val format = Json { ignoreUnknownKeys = true }

        val jsonObjectRequest = @SuppressLint("SetTextI18n")
        object : JsonObjectRequest(
            Method.GET, MrApiUrlUtil().getApiUrl() + "/v2/project/${modId}", null,
            { response ->
                val dataRaw  = response.toString()
                val dataJson = format.decodeFromString<ModrinthProjectModel>(dataRaw)

                projectData = dataJson

                textViewTitle.text     = projectData!!.title
                textViewDownloads.text = resources.getQuantityString(R.plurals.project_downloads, projectData!!.downloads!!.toInt(), nf.format(projectData!!.downloads!!.toLong()).toString())
                textViewFollowers.text = resources.getQuantityString(R.plurals.project_followers, projectData!!.followers!!.toInt(), nf.format(projectData!!.followers!!.toLong()).toString())

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
            },
            { error ->
                Log.e("webCall", error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/${BuildConfig.VERSION_NAME} (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Volley/1.2.1"
                return headers
            }
        }

        RithleSingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(modId: String?) =
            ProjectInfoFragment().apply {
                arguments = Bundle().apply {
                    putString("modId", modId)
                }
            }
    }
}