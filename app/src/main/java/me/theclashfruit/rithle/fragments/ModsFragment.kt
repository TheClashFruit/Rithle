package me.theclashfruit.rithle.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ModsFragment : Fragment() {
    private var currentIndex = 20
    private var lastIndex    = 0

    private var allData: ArrayList<ModrinthSearchHitsModel> = arrayListOf();
    private var recyclerView: RecyclerView?                 = null;

    private var listAdapter: ModListAdapter?        = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_mods, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView)

        listAdapter   = ModListAdapter(allData, requireContext())
        layoutManager = LinearLayoutManager(requireContext())

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter       = listAdapter

        getItems(requireContext())

        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(1))
                    getItems(requireContext())

                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ModsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private fun getItems(context: Context) {
        Log.d("webCall", "funCalled")

        val format = Json { ignoreUnknownKeys = true }

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "https://api.modrinth.com/v2/search?limit=${currentIndex}&offset=${lastIndex}&index=relevance&facets=%5B%5B%22categories%3A%27forge%27%22%2C%22categories%3A%27fabric%27%22%2C%22categories%3A%27quilt%27%22%2C%22categories%3A%27liteloader%27%22%2C%22categories%3A%27modloader%27%22%2C%22categories%3A%27rift%27%22%5D%2C%5B%22project_type%3Amod%22%5D%5D", null,
            { response ->
                currentIndex += 20
                lastIndex    += 20

                Log.d("webCall", "requestComplete")

                val data = format.decodeFromString<ModrinthSearchModel>(response.toString())

                for ((currentPos, hit) in data.hits.withIndex()) {
                    allData.add(hit)

                    listAdapter!!.notifyItemInserted(currentPos)

                    Log.d("webCall", "itemAdd ${hit.title}, ${hit.icon_url}")

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