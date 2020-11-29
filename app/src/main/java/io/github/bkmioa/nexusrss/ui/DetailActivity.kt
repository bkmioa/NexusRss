package io.github.bkmioa.nexusrss.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.format.Formatter
import android.view.Menu
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.android.material.snackbar.Snackbar
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.common.GlideImageGetter
import io.github.bkmioa.nexusrss.db.DownloadDao
import io.github.bkmioa.nexusrss.download.DownloadTask
import io.github.bkmioa.nexusrss.download.RemoteDownloader
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.model.Item
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.ext.android.inject


class DetailActivity : BaseActivity() {
    companion object {
        fun createIntent(context: Context, item: Item): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("item", item)
            return intent
        }
    }

    lateinit var item: Item

    private val downloadDao: DownloadDao by inject()

    private var downloadNodes: List<DownloadNodeModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolBar)

        item = intent.getSerializableExtra("item") as Item

        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar?.title = item.subTitle ?: item.title
        supportActionBar?.subtitle = if (item.subTitle == null) null else item.title

        textView.post {
            textView.setHtml(item.description, GlideImageGetter(textView, Settings.BASE_URL, true))
        }

        textViewInfo.text = "Category :\t${item.category}" + "\n" +
                "Size:\t${
                    Formatter.formatShortFileSize(
                        this, item.enclosure?.length
                            ?: 0
                    )
                }" + "\n" +
                "Author:\t${item.author}" + "\n" +
                "PubDate:\t${item.pubDate}" + "\n"

        downloadDao.getAllLiveData().observe(this){
            downloadNodes = it
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (downloadNodes.isEmpty()) {
            menu.add(R.string.remote_download)
                .setOnMenuItemClickListener {
                    goSetting()
                    true
                }
        } else {
            val subMenu = menu.addSubMenu(R.string.remote_download)
            downloadNodes.forEach { node ->
                subMenu.add(node.name)
                    .setOnMenuItemClickListener {
                        downloadTo(node)
                        true
                    }
            }
        }

        menu.add(R.string.copy_link)
            .setOnMenuItemClickListener {
                copyLink()
                true
            }
        menu.add(R.string.open_link)
            .setOnMenuItemClickListener {
                openLink()
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    private fun openLink() {
        val link = item.link
        Intent(Intent.ACTION_VIEW, Uri.parse(link))
            .run(::startActivity)
    }

    private fun getTorrentUrl() = item.enclosure?.url + "&passkey=" + Settings.PASS_KEY

    private fun copyLink() {
        if (TextUtils.isEmpty(Settings.PASS_KEY)) {
            Snackbar.make(findViewById(Window.ID_ANDROID_CONTENT), R.string.need_pass_key, Snackbar.LENGTH_LONG)
                .setAction(R.string.go_download_setting) {
                    startActivity(Intent(this@DetailActivity, SettingActivity::class.java))
                }
                .show()
            return
        }
        val torrentUrl = getTorrentUrl()
        (getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)
            ?.apply {
                setPrimaryClip(ClipData.newPlainText(torrentUrl, torrentUrl))
                Toast.makeText(application, R.string.copy_done, Toast.LENGTH_SHORT).show()
            }
    }

    private fun downloadTo(node: DownloadNodeModel) {
        if (TextUtils.isEmpty(Settings.PASS_KEY)) {
            goSetting()
            return
        }

        RemoteDownloader.download(applicationContext,node.toDownloadNode(), getTorrentUrl())
    }

    private fun goSetting() {
        Snackbar.make(findViewById(Window.ID_ANDROID_CONTENT), R.string.need_download_setting, Snackbar.LENGTH_LONG)
            .setAction(R.string.go_download_setting) {
                startActivity(Intent(this@DetailActivity, SettingActivity::class.java))
            }
            .show()
    }

}
