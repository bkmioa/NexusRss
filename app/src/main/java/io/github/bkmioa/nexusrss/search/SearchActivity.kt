package io.github.bkmioa.nexusrss.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.databinding.ActivitySearchBinding
import io.github.bkmioa.nexusrss.model.Category
import io.github.bkmioa.nexusrss.ui.ListFragment
import io.github.bkmioa.nexusrss.ui.OptionFragment

class SearchActivity : BaseActivity() {

    private lateinit var searchFragment: ListFragment

    private var searchFilterFragment: OptionFragment? = null
    private var searchFilter: Array<String>? = null
    private var searchPath: String = Category.NORMAL.path

    private val searchHistoryViewModel: SearchHistoryViewModel by viewModels()

    private lateinit var viewBinding: ActivitySearchBinding

    companion object {
        private const val TAG_SEARCH_FILTER = "search_filter"
        private const val TAG_SEARCH = "search"
        private const val TAG_SEARCH_HISTORY = "search_history"

        fun createIntent(context: Context, path: String): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                putExtra("path", path)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolBar)
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_HOME_AS_UP
        }
        searchPath = intent.getStringExtra("path") ?: Category.NORMAL.path
        searchFragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH) as? ListFragment
            ?: ListFragment.newInstance(searchPath, withSearch = true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, searchFragment, TAG_SEARCH)
            .commit()

        viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                query(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchHistoryViewModel.onQuery(newText)
                return false
            }
        })
        viewBinding.searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                addSearchHistoryFragment()
            } else {
                removeSearchHistoryFragment()
            }
        }
        searchHistoryViewModel.selectedKeywordLiveData.observe(this) {
            it.first ?: return@observe

            viewBinding.searchView.setQuery(it.first, it.second)
            WindowCompat.getInsetsController(window, viewBinding.searchView).show(WindowInsetsCompat.Type.ime())
            searchHistoryViewModel.onSelected(null, false)
        }
        viewBinding.searchView.requestFocus()

        //addSearchHistoryFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchFilterMenu = menu.findItem(R.id.action_search_filter)
        searchFilterMenu.setOnMenuItemClickListener {
            if (searchFilterFragment?.isVisible == true) {
                searchFilter = searchFilterFragment?.selected?.toTypedArray()
                removeSearchFilterFragment()
                searchFilterMenu.setIcon(R.drawable.ic_menu_filter)

                val query = viewBinding.searchView.query.toString()
                if (query.isNotBlank()) {
                    query(query)
                } else {
                    viewBinding.searchView.requestFocus()
                    WindowCompat.getInsetsController(window, viewBinding.searchView).show(WindowInsetsCompat.Type.ime())
                }
            } else {
                viewBinding.searchView.clearFocus()
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
            ?: OptionFragment.newInstance(searchPath, searchFilter)

        val fragment = searchFilterFragment ?: throw IllegalStateException()

        if (!fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_top, 0, 0, R.anim.slide_out_to_top)
                .add(R.id.container, fragment, TAG_SEARCH_FILTER)
                .addToBackStack(TAG_SEARCH_FILTER)
                .commit()
        }
    }

    private fun addSearchHistoryFragment() {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH_HISTORY) as? SearchHistoryFragment
            ?: SearchHistoryFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment, TAG_SEARCH_HISTORY)
            .commit()
    }

    private fun removeSearchHistoryFragment() {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH_HISTORY) as? SearchHistoryFragment
            ?: return

        supportFragmentManager.beginTransaction()
            .remove(fragment)
            .commit()
    }

    private fun query(query: String) {
        viewBinding.searchView.clearFocus()
        searchFragment.query(query, searchFilter)
        searchHistoryViewModel.add(query)
    }
}