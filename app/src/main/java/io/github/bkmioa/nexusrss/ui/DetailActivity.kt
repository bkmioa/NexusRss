package io.github.bkmioa.nexusrss.ui

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.text.TextUtils
import android.text.format.Formatter
import android.view.Menu
import android.widget.Toast
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.common.GlideImageGetter
import io.github.bkmioa.nexusrss.di.Injectable
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.repository.UTorrentService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.ResponseBody
import java.net.URLEncoder
import javax.inject.Inject
import android.content.Context.CLIPBOARD_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.content.ClipData
import android.support.design.widget.Snackbar
import android.view.Window


class DetailActivity : BaseActivity(), Injectable {
    companion object {
        fun createIntent(context: Context, item: Item): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("item", item)
            return intent
        }
    }

    @Inject
    internal lateinit
    var service: UTorrentService

    lateinit var item: Item
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
                "Size:\t${Formatter.formatShortFileSize(this, item.enclosure?.length
                        ?: 0)}" + "\n" +
                "Author:\t${item.author}" + "\n" +
                "PubDate:\t${item.pubDate}" + "\n"

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(R.string.remote_download)
                .setOnMenuItemClickListener {
                    download()
                    true
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
        if (TextUtils.isEmpty(Settings.PASS_KEY) ){
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
                    primaryClip = ClipData.newPlainText(torrentUrl, torrentUrl)
                    Toast.makeText(application, R.string.copy_done, Toast.LENGTH_SHORT).show()
                }
    }

    @SuppressLint("CheckResult")
    private fun download() {
        if (TextUtils.isEmpty(Settings.PASS_KEY) ||
                TextUtils.isEmpty(Settings.REMOTE_URL) ||
                TextUtils.isEmpty(Settings.REMOTE_USERNAME) ||
                TextUtils.isEmpty(Settings.REMOTE_PASSWORD)) {
            Snackbar.make(findViewById(Window.ID_ANDROID_CONTENT), R.string.need_download_setting, Snackbar.LENGTH_LONG)
                    .setAction(R.string.go_download_setting) {
                        startActivity(Intent(this@DetailActivity, SettingActivity::class.java))
                    }
                    .show()
            return
        }
        service.token()
                .flatMap {
                    val html = it.string()
                    val token = Regex("<div id='token' style='display:none;'>([^<>]+)</div>")
                            .find(html)?.groupValues?.getOrNull(1) ?: throw IllegalStateException()

                    return@flatMap service.addUrl(token, URLEncoder.encode(getTorrentUrl()))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : Observer<ResponseBody> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        Toast.makeText(application, R.string.add_failure, Toast.LENGTH_SHORT).show()
                    }

                    override fun onNext(response: ResponseBody) {
                        Toast.makeText(application, R.string.add_success, Toast.LENGTH_SHORT).show()
                    }

                })
    }

}
