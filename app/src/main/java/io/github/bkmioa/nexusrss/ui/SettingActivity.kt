package io.github.bkmioa.nexusrss.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.ActionBar
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.databinding.ActivitySettingBinding

class SettingActivity : BaseActivity() {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }

    private lateinit var viewBinding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolBar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        viewBinding.editTextBaseUrl.setText(Settings.BASE_URL)

        viewBinding.editTextBaseUrl.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(s: Editable) {
                Settings.BASE_URL = s.toString().trim().removeSuffix("/")
            }
        })

        viewBinding.editTextApiKey.setText(Settings.API_KEY)
        viewBinding.editTextApiKey.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(s: Editable) {
                Settings.API_KEY = s.toString().trim()
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
