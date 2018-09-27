package io.github.bkmioa.nexusrss.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.model.Tab
import kotlinx.android.synthetic.main.activity_tab_edit.*

class TabEditActivity : BaseActivity() {
    private lateinit var optionFragment: OptionFragment

    companion object {
        fun createIntent(context: Context, tab: Tab? = null): Intent {
            val intent = Intent(context, TabEditActivity::class.java)
            intent.putExtra("tab", tab)
            return intent
        }
    }

    private var tab: Tab? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_edit)

        setSupportActionBar(toolBar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        tab = intent.getParcelableExtra("tab")
        if (tab != null) {
            editTextTitle.setText(tab!!.title)
        }

        optionFragment = supportFragmentManager.findFragmentByTag("options") as? OptionFragment
                ?: OptionFragment.newInstance(tab?.options, true, tab?.columnCount ?: 1)

        supportFragmentManager.beginTransaction()
                .replace(R.id.optionContainer, optionFragment, "options")
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuDone = menu.add("Done")
        menuDone.setIcon(R.drawable.ic_menu_done)
        menuDone.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuDone.setOnMenuItemClickListener {
            done()
            true
        }
        return true
    }

    private fun done() {
        if (editTextTitle.text.isEmpty()) {
            editTextTitle.setError("empty!!")
            return
        }
        val options = optionFragment.selected.toTypedArray()
        val columnCount = optionFragment.columnCount
        val tab: Tab
        if (this.tab == null) {
            tab = Tab(editTextTitle.text.toString(), options, columnCount)
        } else {
            tab = this.tab ?: return
            tab.title = editTextTitle.text.toString()
            tab.options = options
            tab.columnCount = columnCount
        }

        val intent = Intent()
        intent.putExtra("tab", tab)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
