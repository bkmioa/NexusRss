package io.github.bkmioa.nexusrss

import android.app.Application
import androidx.lifecycle.Observer
import com.aitangba.swipeback.ActivityLifecycleHelper
import com.chibatching.kotpref.Kotpref
import io.github.bkmioa.nexusrss.db.AppDao
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.di.appModule
import io.github.bkmioa.nexusrss.model.Tab
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.inject
import java.util.concurrent.TimeUnit


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        Kotpref.init(this)

        registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build())

        Util.buildDynamicShortcuts(this)
    }

}
