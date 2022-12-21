package me.theclashfruit.rithle.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.BuildConfig
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.adapters.ModListAdapter
import me.theclashfruit.rithle.classes.ListDiffCallback
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel
import me.theclashfruit.rithle.models.ModrinthSearchModel

class ListFragment : Fragment() {
    private var currentIndex = 10
    private var lastIndex    = 0

    private var allData: ArrayList<ModrinthSearchHitsModel> = arrayListOf()

    private var listAdapter: ModListAdapter?        = null
    private var layoutManager: LinearLayoutManager? = null

    private var recyclerView: RecyclerView?         = null
    private var nestedScrollView: NestedScrollView? = null

    private var filter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filter = it.getString("projectFilters")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView     = rootView.findViewById(R.id.recyclerView)
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView)

        listAdapter   = ModListAdapter(allData, requireContext(), parentFragmentManager)
        layoutManager = LinearLayoutManager(requireContext())

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter       = listAdapter

        getItems(requireContext())

        nestedScrollView!!.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight)
                getItems(requireContext())
        })

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(projectFilters: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString("projectFilters", projectFilters)
                }
            }
    }

    private fun getItems(context: Context) {
        val format = Json { ignoreUnknownKeys = true }

        Log.d("YesFilter", "https://api.modrinth.com/v2/search?limit=${currentIndex}&offset=${lastIndex}&index=relevance&facets=${filter}")

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "https://api.modrinth.com/v2/search?limit=${currentIndex}&offset=${lastIndex}&index=relevance&facets=${filter}", null,
            { response ->
                currentIndex  = 10
                lastIndex    += 10

                val data = format.decodeFromString<ModrinthSearchModel>(response.toString())

                for (hit in data.hits) {
                    allData.add(hit)

                    val oldAllData = allData

                    DiffUtil.calculateDiff(ListDiffCallback(oldAllData, allData))
                        .dispatchUpdatesTo(listAdapter!!)

                    recyclerView!!.adapter = listAdapter
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

        RithleSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }
}