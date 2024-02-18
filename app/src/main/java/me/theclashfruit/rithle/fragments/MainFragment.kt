package me.theclashfruit.rithle.fragments

import android.accounts.AccountManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.adapters.ModalAdapter
import me.theclashfruit.rithle.databinding.FragmentMainBinding
import me.theclashfruit.rithle.models.ItemWithIcon
import me.theclashfruit.rithle.modrinth.Modrinth
import me.theclashfruit.rithle.modrinth.facets.Category
import me.theclashfruit.rithle.modrinth.facets.FacetBuilder
import me.theclashfruit.rithle.modrinth.facets.ProjectType
import me.theclashfruit.rithle.util.ImageUtil
import org.w3c.dom.Text

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView

    private lateinit var viewPager: ViewPager2

    private lateinit var userName: String
    private lateinit var email: String

    private lateinit var iconDrawable: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        searchBar = binding.searchBar;
        searchView = binding.searchView;

        viewPager = binding.viewPager;

        val modrinthApi = Modrinth.getInstance(requireContext())

        modrinthApi.loggedInUser { user ->
            ImageUtil.loadImage(user.avatarUrl, requireContext()) {
                iconDrawable = it.toBitmapOrNull(128, 128, null)!!

                userName = user.username
                email    = user.email!!

                lifecycleScope.launch(Dispatchers.Main) {
                    searchBar.menu.findItem(R.id.profile).icon = it
                }
            }
        }

        searchBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    val listItems: Array<ItemWithIcon> = arrayOf(
                        ItemWithIcon(R.drawable.ic_close, "Profile"),
                        ItemWithIcon(R.drawable.ic_close, "Create Project"),
                        ItemWithIcon(R.drawable.ic_close, "Collections"),
                        ItemWithIcon(R.drawable.ic_close, "Notifications"),
                        ItemWithIcon(R.drawable.ic_close, "Dashboard"),
                        ItemWithIcon(R.drawable.ic_close, "Settings"),
                        ItemWithIcon(R.drawable.ic_close, "Logout")
                    )

                    val view = layoutInflater.inflate(R.layout.profile_modal, null)

                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                    val imageView = view.findViewById<ImageView>(R.id.imageView)

                    val userNameTextView = view.findViewById<TextView>(R.id.textView4)
                    val emailTextView = view.findViewById<TextView>(R.id.textView5)

                    val adapter = ModalAdapter(listItems)

                    Log.d("RithleApp", adapter.itemCount.toString())

                    MaterialAlertDialogBuilder(requireContext())
                        .setView(view)
                        .show()

                    imageView.setImageBitmap(iconDrawable)

                    recyclerView.adapter = adapter
                    recyclerView.refreshDrawableState()

                    userNameTextView.text = userName
                    emailTextView.text    = email



                    /* .setItems(arrayOf("Profile", "Create Project", "Collections", "Notifications", "Dashboard", "Settings", "Logout")) { dialog, which ->
                            dialog.dismiss()
                        }*/

                    true
                }
                else -> false
            }
        }

        val listView = binding.listView

        val facets =
            FacetBuilder()
                .addCategories(Category.ALL_MOD_LOADERS)
                .addProjectType(ProjectType.MOD)
                .build()

        searchView
            .editText
            .setOnEditorActionListener { v, _, _ ->
                Log.d("RithleApp", "Search: ${v.text}")

                searchBar.setText(searchView.text);
                searchView.hide();

                false
            }


        searchView
            .editText
            .addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    modrinthApi.search(searchView.editText.text.toString(), facets) {
                        val list = mutableListOf<String>()

                        it.forEach { project ->
                            list.add(project.title)
                        }

                        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)

                        listView.refreshDrawableState()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {}
            })

        return binding.root
    }

    companion object
}