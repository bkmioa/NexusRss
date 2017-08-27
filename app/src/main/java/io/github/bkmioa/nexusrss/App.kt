package io.github.bkmioa.nexusrss

import android.app.Activity
import com.chibatching.kotpref.Kotpref
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerApplication
import io.github.bkmioa.nexusrss.di.DaggerAppComponent
import javax.inject.Inject


class App : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        Kotpref.init(this)
    }

}
