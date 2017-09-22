package io.github.bkmioa.nexusrss.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.model.Tab
import kotlinx.android.synthetic.main.activity_tab_edit.*

class TabEditActivity : BaseActivity() {

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

        var fragment = supportFragmentManager.findFragmentByTag("options") as? OptionFragment
        if (fragment == null) {
            fragment = OptionFragment.newInstance(tab)
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.optionContainer, fragment, "options")
                .commit()

        buttonOk.setOnClickListener {
            if (editTextTitle.text.isEmpty()) {
                editTextTitle.setError("empty!!")
                return@setOnClickListener
            }
            val options = fragment!!.selected.toTypedArray()
            val tab: Tab
            if (this.tab == null) {
                tab = Tab(editTextTitle.text.toString(), options)
            } else {
                tab = this.tab!!
                tab.title = editTextTitle.text.toString()
                tab.options = options
            }

            val intent = Intent()
            intent.putExtra("tab", tab)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}