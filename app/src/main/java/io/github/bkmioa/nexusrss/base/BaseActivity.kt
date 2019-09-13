package io.github.bkmioa.nexusrss.base

import android.view.MenuItem
import com.aitangba.swipeback.SwipeBackActivity

open class BaseActivity : SwipeBackActivity() {

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
