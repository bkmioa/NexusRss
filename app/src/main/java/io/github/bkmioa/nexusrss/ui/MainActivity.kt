package io.github.bkmioa.nexusrss.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.common.Scrollable
import io.github.bkmioa.nexusrss.di.Injectable
import io.github.bkmioa.nexusrss.model.Tab
import io.github.bkmioa.nexusrss.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


class MainActivity : BaseActivity(), Injectable {

    @Inject lateinit internal
    var mainViewModel: MainViewModel

    val tabs = ArrayList<Tab>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolBar)

        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            private val mappingFragment = WeakHashMap<Tab, ListFragment>()

            override fun getCount() = tabs.size

            override fun getPageTitle(position: Int) = tabs[position].title

            override fun getItemId(position: Int) = tabs[position].id!!.toLong()

            override fun getItem(position: Int): Fragment {
                val tab = tabs[position]
                val fragment = ListFragment.newInstance(tab.options)
                mappingFragment.put(tab, fragment)
                return fragment
            }

            override fun destroyItem(container: ViewGroup?, position: Int, any: Any?) {
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
                val item = viewPager.adapter.instantiateItem(viewPager, tab.position)
                if (item is Scrollable) {
                    item.scrollToTop()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {}

        })

        mainViewModel.tabs().observe(this, Observer<Array<Tab>> {
            val list = it!!.filter { it.isShow }

            tabs.clear()
            tabs.addAll(list.sorted())

            buildTabs()
        })

    }

    private fun buildTabs() {
        tabLayout.removeAllTabs()
        tabs.forEach { tabLayout.newTab() }

        viewPager.adapter.notifyDataSetChanged()
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
        return true
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
        else -> super.onOptionsItemSelected(item)
    }

    override fun finish() {
        //super.finish()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}

