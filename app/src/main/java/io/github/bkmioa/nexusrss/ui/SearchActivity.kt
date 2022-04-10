package io.github.bkmioa.nexusrss.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {

    private lateinit var searchFragment: ListFragment

    private var searchFilterFragment: OptionFragment? = null
    private var searchFilter: Array<String>? = null

    companion object {
        private const val TAG_SEARCH_FILTER = "search_filter"
        private const val TAG_SEARCH = "search"

        fun createIntent(context: Context, path: String): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                putExtra("path", path)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolBar)
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_HOME_AS_UP
        }
        val path = intent.getStringExtra("path") ?: "torrents.php"
        searchFragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH) as? ListFragment
            ?: ListFragment.newInstance(path, withSearch = true)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, searchFragment, TAG_SEARCH)
                .commit()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                query(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false

        })
        searchView.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchFilterMenu = menu.findItem(R.id.action_search_filter)
        searchFilterMenu.setOnMenuItemClickListener {
            if (searchFilterFragment?.isVisible == true) {
                searchFilter = searchFilterFragment?.selected?.toTypedArray()
                removeSearchFilterFragment()
                searchFilterMenu.setIcon(R.drawable.ic_menu_filter)

                val query = searchView.query.toString()
                if (query.isNotBlank()) {
                    query(query)
                } else {
                    searchView.requestFocus()
                    ContextCompat.getSystemService(this, InputMethodManager::class.java)
                            ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
            } else {
                searchView.clearFocus()
                addSearchFilterFragment()
                searchFilterMenu.setIcon(R.drawable.ic_menu_done)
            }
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun removeSearchFilterFragment() {
        supportFragmentManager.popBackStack(TAG_SEARCH_FILTER, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        searchFilterFragment = null
    }

    private fun addSearchFilterFragment() {
        searchFilterFragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH_FILTER) as? OptionFragment
                ?: OptionFragment.newInstance(searchFilter)

        val fragment = searchFilterFragment ?: throw IllegalStateException()

        if (!fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_from_top, 0, 0, R.anim.slide_out_to_top)
                    .add(R.id.container, fragment, TAG_SEARCH_FILTER)
                    .addToBackStack(TAG_SEARCH_FILTER)
                    .commit()
        }
    }

    private fun query(query: String) {
        searchView.clearFocus()
        searchFragment.query(query, searchFilter)
    }
}