package io.github.bkmioa.nexusrss.base

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
