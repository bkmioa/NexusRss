package io.github.bkmioa.nexusrss.base

import android.content.Context
import android.support.v4.app.Fragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.github.bkmioa.nexusrss.di.Injectable
import javax.inject.Inject

open class BaseFragment : Fragment(), HasSupportFragmentInjector {
    @Inject
    internal lateinit
    var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context) {
        if (this is Injectable) {
            AndroidSupportInjection.inject(this)
        }
        super.onAttach(context)
    }

    override fun supportFragmentInjector() = childFragmentInjector


}
