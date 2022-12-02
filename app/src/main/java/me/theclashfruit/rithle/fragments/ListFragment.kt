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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.adapters.ModListAdapter
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel
import me.theclashfruit.rithle.models.ModrinthSearchModel

class ListFragment : Fragment() {
    private var currentIndex = 10
    private var lastIndex    = 0

    private var allData: ArrayList<ModrinthSearchHitsModel> = arrayListOf();

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
        Log.d("webCall", "funCalled")

        val format = Json { ignoreUnknownKeys = true }

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "https://api.modrinth.com/v2/search?limit=${currentIndex}&offset=${lastIndex}&index=relevance&facets=${filter}", null,
            { response ->
                currentIndex  = 5
                lastIndex    += 5

                Log.d("webCall", "requestComplete")
                Log.d("webCall", "pageIndexes cI: $currentIndex : lI: $lastIndex")
                Log.d("webCall", "webUrl https://api.modrinth.com/v2/search?limit=${currentIndex}&offset=${lastIndex}&index=relevance&facets=%5B%5B%22categories%3A%27forge%27%22%2C%22categories%3A%27fabric%27%22%2C%22categories%3A%27quilt%27%22%2C%22categories%3A%27liteloader%27%22%2C%22categories%3A%27modloader%27%22%2C%22categories%3A%27rift%27%22%5D%2C%5B%22project_type%3Amod%22%5D%5D")

                val data = format.decodeFromString<ModrinthSearchModel>(response.toString())

                for ((currentPos, hit) in data.hits.withIndex()) {
                    allData.add(hit)

                    listAdapter!!.notifyItemInserted(currentPos)

                    recyclerView!!.adapter = listAdapter

                    Log.d("webCall", "itemAdd ${hit.title}, ${hit.icon_url}")
                    Log.d("webCall", "allData $allData")

                }
            },
            { error ->
                // TODO: Handle error
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}) Rithle/0.0.1 (github.com/TheClashFruit/Rithle; admin@theclashfruit.me) Fuel/2.3.1"
                return headers
            }
        }

        RithleSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }
}