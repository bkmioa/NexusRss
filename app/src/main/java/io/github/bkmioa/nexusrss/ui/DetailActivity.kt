package io.github.bkmioa.nexusrss.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.text.format.Formatter
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.common.GlideImageGetter
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.model.Item
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.*
import java.io.IOException

class DetailActivity : BaseActivity() {
    companion object {
        fun createIntent(context: Context, item: Item): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("item", item)
            return intent
        }
    }

    lateinit var item: Item
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolBar)

        item = intent.getSerializableExtra("item") as Item

        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar?.title = item.title
        supportActionBar?.subtitle = item.subTitle

        textView.setHtml(item.description, GlideImageGetter(textView, Settings.BASE_URL, true))

        textViewInfo.text = "Category :\t${item.category}" + "\n" +
                "Size:\t${Formatter.formatShortFileSize(this, item.enclosure?.length!!)}" + "\n" +
                "Author:\t${item.author}" + "\n" +
                "PubDate:\t${item.pubDate}" + "\n"

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("下载")
                .setOnMenuItemClickListener {
                    download()
                    true
                }
        return super.onCreateOptionsMenu(menu)
    }

    private fun download() {
        val client = OkHttpClient.Builder().build()
        val body = FormBody.Builder()
                .addEncoded("url", item.enclosure?.url)
                .build()
        val request = Request.Builder()
                .post(body)
                .build()
        client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        runOnUiThread {
                            Toast.makeText(application, "添加失败", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        runOnUiThread {
                            Toast.makeText(application, response?.body()?.string() + "添加成功", Toast.LENGTH_SHORT).show()
                        }
                    }

                })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
