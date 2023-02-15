package io.github.bkmioa.nexusrss

import android.app.Application
import com.aitangba.swipeback.ActivityLifecycleHelper
import com.chibatching.kotpref.Kotpref
import io.github.bkmioa.nexusrss.di.appModule
import io.github.bkmioa.nexusrss.login.VerifyManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        Kotpref.init(this)
        VerifyManager.init(this)
        registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build())
    }

}
