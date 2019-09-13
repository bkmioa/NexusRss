package io.github.bkmioa.nexusrss

import android.app.Application
import com.aitangba.swipeback.ActivityLifecycleHelper
import com.chibatching.kotpref.Kotpref
import io.github.bkmioa.nexusrss.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        Kotpref.init(this)

        registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build())
    }

}
