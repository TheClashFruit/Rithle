package me.theclashfruit.rithle.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel

class ProjectViewFragment : Fragment() {
    private var modId: String? = null
    private var modData: ModrinthSearchHitsModel? = null

    private val tabTexts = listOf("Description", "Gallery", "Changelog", "Versions")
    private val tabTextsNoGallery = listOf("Description", "Changelog", "Versions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val format = Json { ignoreUnknownKeys = true }

        arguments?.let {
            modId = it.getString("modId")
            modData = format.decodeFromString<ModrinthSearchHitsModel>(it.getString("modData")!!)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_project_view, container, false)

        val toolBar: MaterialToolbar           = rootView.findViewById(R.id.toolbar)

        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = rootView.findViewById<ViewPager2>(R.id.viewPager)

        if(modData!!.featured_gallery == null)
            tabLayout.removeTabAt(1)

        toolBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewPager.adapter = ScreenSlidePagerAdapter(this, modData!!.featured_gallery != null)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if(modData!!.featured_gallery != null) {
                tab.text = tabTexts[position]
            } else {
                tab.text = tabTextsNoGallery[position]
            }
        }.attach()

        Log.d("assss",
            ScreenSlidePagerAdapter(this, modData!!.featured_gallery != null).itemCount.toString()
        )

        Log.d("assss",
            tabLayout.tabCount.toString()
        )

        return rootView
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment, hasGallery: Boolean) : FragmentStateAdapter(fa) {
        private val hasGallery = hasGallery

        val length: Int = if(hasGallery) 4 else 3

        override fun getItemCount(): Int = length

        override fun createFragment(position: Int) : Fragment {
            val tabbyTexty = if(hasGallery) tabTexts else tabTextsNoGallery

            when(tabbyTexty[position]) {
                "Description" -> {
                    return ProjectInfoFragment.newInstance(modId)
                }
                "Gallery" -> {
                    return ProjectChangelogFragment.newInstance(modId)
                }
                "Changelog" -> {
                    return ProjectChangelogFragment.newInstance(modId)
                }
                "Versions" -> {
                    return ProjectDownloadsFragment.newInstance(modId)
                }
                else -> {
                    return ProjectChangelogFragment.newInstance(modId)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(modId: String, modData: String) =
            ProjectViewFragment().apply {
                arguments = Bundle().apply {
                    putString("modId", modId)
                    putString("modData", modData)
                }
            }
    }
}