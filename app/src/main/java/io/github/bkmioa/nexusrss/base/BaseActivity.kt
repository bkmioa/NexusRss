package io.github.bkmioa.nexusrss.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.aitangba.swipeback.SwipeBackActivity
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import io.github.bkmioa.nexusrss.di.Injectable
import javax.inject.Inject

open class BaseActivity : SwipeBackActivity(),
        HasFragmentInjector, HasSupportFragmentInjector {

    @Inject
    internal lateinit
    var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun fragmentInjector() = frameworkFragmentInjector


    @Inject
    internal lateinit
    var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

    override fun supportFragmentInjector() = supportFragmentInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        if (this is Injectable) {
            AndroidInjection.inject(this)
        }
        super.onCreate(savedInstanceState)
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
