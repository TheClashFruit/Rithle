package me.theclashfruit.rithle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.serialization.responseObject
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.adapters.ModListAdapter
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel
import me.theclashfruit.rithle.models.ModrinthSearchModel

class MainActivity : AppCompatActivity() {
    private var currentIndex = 20
    private var lastIndex    = 0

    private var allData: ArrayList<ModrinthSearchHitsModel> = arrayListOf();
    private val recyclerView: RecyclerView? = null;

    private val listAdapter   = ModListAdapter(allData)
    private val layoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter       = listAdapter

        //FuelManager.instance.basePath = "https://api.modrinth.com/v2"

        getItems()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(1))
                    getItems()

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun getItems() {
        Log.d("webCall", "funCalled")

        val format = Json { ignoreUnknownKeys = true }

        Fuel.get("https://api.modrinth.com/v2/search?limit=${currentIndex}&offset=${lastIndex}&index=relevance&facets=%5B%5B%22categories%3A%27forge%27%22%2C%22categories%3A%27fabric%27%22%2C%22categories%3A%27quilt%27%22%2C%22categories%3A%27liteloader%27%22%2C%22categories%3A%27modloader%27%22%2C%22categories%3A%27rift%27%22%5D%2C%5B%22project_type%3Amod%22%5D%5D")
            .responseObject<ModrinthSearchModel>(json = format) { _, _, result ->
                currentIndex += 20
                lastIndex    += 20

                Log.d("webCall", "requestComplete")

                val data = result.get()

                for ((currentPos, hit) in data.hits.withIndex()) {
                    allData.add(hit)

                    listAdapter.notifyItemInserted(currentPos)

                    Log.d("webCall", "itemAdd ${hit.title}")

                }
            }
    }

    /*

    val data = result.get()
    Log.d("respii", data)

     */
}