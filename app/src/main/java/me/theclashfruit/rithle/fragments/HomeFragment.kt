package me.theclashfruit.rithle.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.theclashfruit.rithle.classes.FilterBuilder
import me.theclashfruit.rithle.R

class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        val bottomNav = rootView.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val toolBar   = rootView.findViewById<MaterialToolbar>(R.id.toolbar)

        val filter = FilterBuilder()
            .setProjectType("mod")
            .addFilterItem("categories:'forge'")
            .addFilterItem("categories:'fabric'")
            .addFilterItem("categories:'quilt'")
            .addFilterItem("categories:'liteloader'")
            .addFilterItem("categories:'modloader'")
            .addFilterItem("categories:'rift'")
            .build()

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        var listFragment = ListFragment.newInstance(filter)

        fragmentTransaction
            .replace(R.id.fragmentContainer, listFragment)
            .commit()

        Log.w("YesFilter", filter)

        toolBar.setOnMenuItemClickListener { item ->
            val toolBarFragmentTransaction = parentFragmentManager.beginTransaction()

            when(item.itemId) {
                R.id.toolBarSearch -> {
                    return@setOnMenuItemClickListener true
                }
                R.id.toolBarAccount -> {
                    return@setOnMenuItemClickListener true
                }
                R.id.toolBarSettings -> {
                    val settingsFragment = SettingsFragment.newInstance()

                    toolBarFragmentTransaction
                        .addToBackStack("settingsFragment")
                        .add(R.id.parentFragmentContainer, settingsFragment)
                        .commit()

                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }

        bottomNav.setOnNavigationItemSelectedListener { item ->
            val bottomNavFragmentTransaction = parentFragmentManager.beginTransaction()

            when(item.itemId) {
                R.id.itemMods -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("mod")
                        .addFilterItem("categories:'forge'")
                        .addFilterItem("categories:'fabric'")
                        .addFilterItem("categories:'quilt'")
                        .addFilterItem("categories:'liteloader'")
                        .addFilterItem("categories:'modloader'")
                        .addFilterItem("categories:'rift'")
                        .build()

                    listFragment = ListFragment.newInstance(fragmentFilter)

                    bottomNavFragmentTransaction
                        .replace(R.id.fragmentContainer, listFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemPlugins -> {
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

                    listFragment = ListFragment.newInstance(fragmentFilter)

                    bottomNavFragmentTransaction
                        .replace(R.id.fragmentContainer, listFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemResourcePacks -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("resourcepack")
                        .build()

                    listFragment = ListFragment.newInstance(fragmentFilter)

                    bottomNavFragmentTransaction
                        .replace(R.id.fragmentContainer, listFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.itemModpacks -> {
                    val fragmentFilter = FilterBuilder()
                        .setProjectType("modpack")
                        .build()

                    listFragment = ListFragment.newInstance(fragmentFilter)

                    bottomNavFragmentTransaction
                        .replace(R.id.fragmentContainer, listFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

        return rootView
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