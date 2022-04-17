package io.github.bkmioa.nexusrss.download.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBar
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import kotlinx.android.synthetic.main.activity_download_edit.*

class DownloadEditActivity : BaseActivity() {
    companion object {
        const val KEY_DOWNLOAD_NODE = "download_node"

        fun createIntent(context: Context, downloadNode: DownloadNodeModel? = null): Intent {
            return Intent(context, DownloadEditActivity::class.java).apply {
                putExtra(KEY_DOWNLOAD_NODE, downloadNode)
            }
        }
    }

    private val downloadNode: DownloadNodeModel? by lazy { intent.getParcelableExtra(KEY_DOWNLOAD_NODE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_edit)
        setSupportActionBar(toolBar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        val types = DownloadNodeModel.ALL_TYPES
        typeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        downloadNode?.let {
            typeSpinner.setSelection(types.indexOf(it.type))
            editTextName.setText(it.name)
            editTextRemoteUrl.setText(it.host)
            editTextUsername.setText(it.userName)
            editTextPassword.setText(it.password)
            editTextPath.setText(it.defaultPath)
        }
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
        val name = editTextName.text.toString().trim()
        val host = editTextRemoteUrl.text.toString().trim()
        val userName = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val path = editTextPath.text.toString().trim()
        val type = typeSpinner.selectedItem.toString()

        if (host.isEmpty()) {
            editTextRemoteUrl.error = getString(R.string.edit_can_not_empty)
            return
        }

        val downloadNode = DownloadNodeModel(name, host, userName, password, type, path, downloadNode?.id)

        val intent = Intent()
        intent.putExtra(KEY_DOWNLOAD_NODE, downloadNode)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}