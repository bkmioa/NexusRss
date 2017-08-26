package io.github.bkmioa.nexusrss.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuItem
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.common.Scrollable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolBar)


        val tabs = Settings.tabs
        viewPager.offscreenPageLimit = tabs.size - 1
        tabs.forEach { tabLayout.newTab() }
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount() = tabs.size

            override fun getItem(position: Int) = ListFragment.newInstance(tabs[position].options)
            override fun getPageTitle(position: Int): CharSequence {
                return tabs[position].title
            }
        }
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout.post {
            if (!tabLayout.shouldDelayChildPressedState()) {
                tabLayout.tabMode = TabLayout.MODE_FIXED
            }
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                val item = viewPager.adapter.instantiateItem(viewPager, tab.position)
                if (item is Scrollable) {
                    item.scrollToTop()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(SettingActivity.createIntent(this))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun finish() {
        //super.finish()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}

