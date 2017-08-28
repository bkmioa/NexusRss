package io.github.bkmioa.nexusrss.base

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Context
import android.support.v4.app.Fragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.github.bkmioa.nexusrss.di.Injectable
import javax.inject.Inject

open class BaseFragment : Fragment(), HasSupportFragmentInjector, LifecycleRegistryOwner {
    private val mRegistry by lazy { LifecycleRegistry(this) }

    @Inject lateinit internal
    var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun getLifecycle() = mRegistry

    override fun onAttach(context: Context) {
        if (this is Injectable) {
            AndroidSupportInjection.inject(this)
        }
        super.onAttach(context)
    }

    override fun supportFragmentInjector() = childFragmentInjector


}
