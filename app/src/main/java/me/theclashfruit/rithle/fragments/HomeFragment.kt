package me.theclashfruit.rithle.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import me.theclashfruit.rithle.classes.FilterBuilder
import me.theclashfruit.rithle.R

class HomeFragment : Fragment() {
    private val tabTexts = listOf("Mods", "Plugins", "Data Packs", "Shader Packs", "Resource Packs", "Modpacks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView  = inflater.inflate(R.layout.fragment_home, container, false)

        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = rootView.findViewById<ViewPager2>(R.id.viewPager)

        val toolBar   = rootView.findViewById<MaterialToolbar>(R.id.toolbar)

        viewPager.adapter = ScreenSlidePagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTexts[position]
        }.attach()

        toolBar.setOnMenuItemClickListener { item ->
            val toolBarFragmentTransaction = parentFragmentManager.beginTransaction()

            when(item.itemId) {
                R.id.toolBarSearch -> {
                    val searchFragment = SearchFragment.newInstance("a", "b")

                    toolBarFragmentTransaction
                        .addToBackStack("searchFragment")
                        .add(R.id.parentFragmentContainer, searchFragment)
                        .commit()

                    return@setOnMenuItemClickListener true
                }
                R.id.toolBarAccount -> {
                    val userFragment = UserFragment.newInstance()

                    toolBarFragmentTransaction
                        .addToBackStack("userFragment")
                        .add(R.id.parentFragmentContainer, userFragment)
                        .commit()

                    return@setOnMenuItemClickListener true
                }
                R.id.toolBarSettings -> {
                    val settingsFragment = SettingsContainerFragment.newInstance()

                    toolBarFragmentTransaction
                        .addToBackStack("settingsFragment")
                        .add(R.id.parentFragmentContainer, settingsFragment)
                        .commit()

                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }

        return rootView
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 6

        override fun createFragment(position: Int) : Fragment {
            when(tabTexts[position]) {
                "Mods" -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("mod")
                        .addFilterItem("categories:'forge'")
                        .addFilterItem("categories:'fabric'")
                        .addFilterItem("categories:'quilt'")
                        .addFilterItem("categories:'liteloader'")
                        .addFilterItem("categories:'modloader'")
                        .addFilterItem("categories:'rift'")
                        .build()

                    return ListFragment.newInstance(fragmentFilter)
                }

                "Plugins" -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("mod")
                        .addFilterItem("categories:'bukkit'")
                        .addFilterItem("categories:'spigot'")
                        .addFilterItem("categories:'paper'")
                        .addFilterItem("categories:'purpur'")
                        .addFilterItem("categories:'sponge'")
                        .addFilterItem("categories:'bungeecord'")
                        .addFilterItem("categories:'waterfall'")
                        .addFilterItem("categories:'velocity'")
                        .build()

                    return ListFragment.newInstance(fragmentFilter)
                }

                "Data Packs" -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("mod")
                        .addFilterItem("categories:'datapack'")
                        .build()

                    return ListFragment.newInstance(fragmentFilter)
                }

                "Shader Packs" -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("shader")
                        .build()

                    return ListFragment.newInstance(fragmentFilter)
                }

                "Resource Packs" -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("resourcepack")
                        .build()

                    return ListFragment.newInstance(fragmentFilter)
                }

                "Modpacks" -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("modpack")
                        .build()

                    return ListFragment.newInstance(fragmentFilter)
                }

                else -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("mod")
                        .addFilterItem("categories:'forge'")
                        .addFilterItem("categories:'fabric'")
                        .addFilterItem("categories:'quilt'")
                        .addFilterItem("categories:'liteloader'")
                        .addFilterItem("categories:'modloader'")
                        .addFilterItem("categories:'rift'")
                        .build()

                    return ListFragment.newInstance(fragmentFilter)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}