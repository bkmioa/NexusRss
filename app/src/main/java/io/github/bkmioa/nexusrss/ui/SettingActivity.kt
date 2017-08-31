package io.github.bkmioa.nexusrss.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.text.Editable
import android.text.TextWatcher
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolBar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        editTextPasskey.setText(Settings.PASS_KEY)
        editTextRemoteUrl.setText(Settings.REMOTE_URL)
        editTextUsername.setText(Settings.REMOTE_USERNAME)
        editTextPassword.setText(Settings.REMOTE_PASSWORD)

        editTextPasskey.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(s: Editable) {
                Settings.PASS_KEY = s.toString().trim()
            }
        })
        editTextRemoteUrl.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(s: Editable) {
                Settings.REMOTE_URL = s.toString().trim()
            }
        })
        editTextUsername.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(s: Editable) {
                Settings.REMOTE_USERNAME = s.toString().trim()
            }
        })
        editTextPassword.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(s: Editable) {
                Settings.REMOTE_PASSWORD = s.toString().trim()
            }
        })

    }

    abstract class OnTextChange : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }
    }
}
