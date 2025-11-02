package io.github.bkmioa.nexusrss

import android.app.Application
import android.os.Build
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.airbnb.mvrx.Mavericks
import com.chibatching.kotpref.Kotpref
import io.github.bkmioa.nexusrss.di.appModule
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named


class App : Application(), SingletonImageLoader.Factory, KoinComponent {

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
        Mavericks.initialize(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val httpClient: OkHttpClient by inject(named("coil"))

        return ImageLoader.Builder(this)
            .crossfade(true)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { httpClient }))
                if (Build.VERSION.SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

}
