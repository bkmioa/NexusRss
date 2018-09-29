package io.github.bkmioa.nexusrss.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import io.github.bkmioa.nexusrss.BuildConfig
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.common.Scrollable
import io.github.bkmioa.nexusrss.di.Injectable
import io.github.bkmioa.nexusrss.model.Release
import io.github.bkmioa.nexusrss.model.Tab
import io.github.bkmioa.nexusrss.viewmodel.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


class MainActivity : BaseActivity(), Injectable {

    @Inject
    internal
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit
    var mainViewModel: MainViewModel

    private val tabs = ArrayList<Tab>()

    private lateinit var searchView: SearchView

    private var searchFragment: ListFragment? = null
    private var searchFilterFragment: OptionFragment? = null
    private var searchFilter: Array<String>? = null

    override fun supportSlideBack() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolBar)

        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            private val mappingFragment = WeakHashMap<Tab, ListFragment>()

            override fun getCount() = tabs.size

            override fun getPageTitle(position: Int) = tabs[position].title

            override fun getItemId(position: Int) = tabs[position].hashCode().toLong()

            override fun getItem(position: Int): Fragment {
                val tab = tabs[position]
                val fragment = ListFragment.newInstance(tab.options, false, tab.columnCount)
                mappingFragment.put(tab, fragment)
                return fragment
            }

            override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
                super.destroyItem(container, position, any)
                val iterator = mappingFragment.iterator()
                while (iterator.hasNext()) {
                    if (iterator.next() === any) {
                        iterator.remove()
                    }
                }
            }

            override fun getItemPosition(o: Any): Int {
                mappingFragment.forEach {
                    if (it.value == o) {
                        val position = tabs.indexOf(it.key)
                        if (position != -1) {
                            return position
                        }
                    }
                }
                return PagerAdapter.POSITION_NONE
            }

        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                val item = viewPager.adapter?.instantiateItem(viewPager, tab.position)
                if (item is Scrollable) {
                    item.scrollToTop()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {}

        })

        mainViewModel.tabs().observe(this, Observer<Array<Tab>> {
            val list = it?.filter { it.isShow } ?: throw IllegalStateException()

            tabs.clear()
            tabs.addAll(list.sorted())

            buildTabs()
        })

    }

    private fun buildTabs() {
        tabLayout.removeAllTabs()
        tabs.forEach { tabLayout.newTab() }

        viewPager.adapter?.notifyDataSetChanged()
        viewPager.offscreenPageLimit = tabs.size - 1

        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout.post {
            if (!tabLayout.shouldDelayChildPressedState()) {
                tabLayout.tabMode = TabLayout.MODE_FIXED
            }

            //add long click listener
            val viewGroup = tabLayout.getChildAt(0) as ViewGroup
            for (i in 0 until viewGroup.childCount) {
                viewGroup.getChildAt(i).setOnLongClickListener {
                    onTabLongClicked(i)
                    true
                }
            }
        }


    }

    private fun onTabLongClicked(position: Int) {
        //todo edit tab
//        val intent = TabEditActivity.createIntent(this, tabs[position])
//        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchFilterMenu = menu.findItem(R.id.action_search_filter)
        searchFilterMenu.setOnMenuItemClickListener {
            if (searchFilterFragment?.isVisible == true) {
                searchFilter = searchFilterFragment?.selected?.toTypedArray()
                removeSearchFilterFragment()
                searchFilterMenu.setIcon(R.drawable.ic_menu_filter)
            } else {
                addSearchFilterFragment()
                searchFilterMenu.setIcon(R.drawable.ic_menu_done)
            }
            true
        }
        val menuSearch = menu.findItem(R.id.action_search)
        MenuItemCompat.setOnActionExpandListener(menuSearch, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchFilterMenu.isVisible = true
                addSearchFragment()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                searchFilterMenu.isVisible = false
                removeSearchFragment()
                removeSearchFilterFragment()
                searchFilterMenu.setIcon(R.drawable.ic_menu_filter)
                searchFilter = null
                return true
            }

        })
        searchView = menuSearch.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                onQueryText(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false

        })
        return true
    }

    private fun removeSearchFilterFragment() {
        supportFragmentManager.popBackStack("search_filter", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        searchFilterFragment = null
    }

    private fun addSearchFilterFragment() {
        searchFilterFragment = supportFragmentManager.findFragmentByTag("search_filter") as? OptionFragment
                ?: OptionFragment.newInstance(searchFilter)

        val fragment = searchFilterFragment ?: throw IllegalStateException()

        if (!fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_from_bottom, 0, 0, R.anim.slide_out_to_bottom)
                    .add(R.id.container, fragment, "search_filter")
                    .addToBackStack("search_filter")
                    .commit()
        }
    }

    private fun removeSearchFragment() {
        supportFragmentManager.popBackStack("search", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        searchFragment = null
    }

    private fun addSearchFragment() {
        searchFragment = supportFragmentManager.findFragmentByTag("search") as? ListFragment
                ?: ListFragment.newInstance(withSearch = true)

        val fragment = searchFragment ?: throw IllegalStateException()

        if (!fragment.isVisible) {
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_from_bottom, 0, 0, R.anim.slide_out_to_bottom)
                    .add(R.id.container, fragment, "search")
                    .addToBackStack("search")
                    .commit()
        }
    }

    private fun onQueryText(query: String?) {
        searchView.clearFocus()
        searchFragment?.query(query, searchFilter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(SettingActivity.createIntent(this))
            true
        }
        R.id.action_tabs -> {
            startActivity(TabListActivity.createIntent(this))
            true
        }
        R.id.action_checking_version -> {
            checkingVersion()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("CheckResult")
    private fun checkingVersion() {
        mainViewModel.checkNewVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::checkNewVersion, Throwable::printStackTrace)
    }

    private fun checkNewVersion(release: Array<Release>?) {
        release?.firstOrNull()
                ?.takeIf { it.name.toLowerCase() > "v${BuildConfig.VERSION_NAME}" }
                ?.apply(::hasNewVersion)
    }

    private fun hasNewVersion(release: Release) {
        AlertDialog.Builder(this)
                .setTitle(release.name)
                .setMessage(release.body)
                .setPositiveButton(R.string.download) { _, _ ->
                    val url = release.assets.firstOrNull()
                            ?.takeIf { it.contentType == Release.Asset.TYPE_APK }
                            ?.browserDownloadUrl ?: release.htmlUrl
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                .setNegativeButton(R.string.cancel, null)
                .show()

    }

    override fun finish() {
        //super.finish()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}

