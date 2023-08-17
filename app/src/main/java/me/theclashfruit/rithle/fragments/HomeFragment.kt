package me.theclashfruit.rithle.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.search.SearchBar
import me.theclashfruit.rithle.R


class HomeFragment : Fragment() {

    var searchBar: SearchBar? = null;
    var navView: NavigationView? = null;
    var navLayout: DrawerLayout? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_home, container, false)

        searchBar  = inflatedView.findViewById(R.id.search_bar)
        navView    = inflatedView.findViewById(R.id.navView)
        navLayout  = inflatedView.findViewById(R.id.drawerLayout)

        searchBar!!.inflateMenu(R.menu.main_menu)
        searchBar!!.setNavigationIcon(R.drawable.ic_menu)

        searchBar!!.setOnMenuItemClickListener { menuItem -> true }

        /*
        val drawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            navLayout,
            toolBar,
            R.string.app_name,
            R.string.app_name
        )
        */

        /*
        navLayout!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        */

        navView!!.setNavigationItemSelectedListener(::onNavigationItemSelected)

        // drawerToggle.setHomeAsUpIndicator(R.drawable.ic_search)

        return inflatedView
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemMinecraft -> {
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}