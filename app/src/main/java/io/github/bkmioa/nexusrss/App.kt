package io.github.bkmioa.nexusrss

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.airbnb.mvrx.Mavericks
import com.chibatching.kotpref.Kotpref
import io.github.bkmioa.nexusrss.di.appModule
import io.github.bkmioa.nexusrss.login.VerifyManager
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named


class App : Application(), ImageLoaderFactory, KoinComponent {

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
        Mavericks.initialize(this)
    }

    override fun newImageLoader(): ImageLoader {
        val httpClient: OkHttpClient by inject(named("coil"))

        return ImageLoader.Builder(this)
            .crossfade(true)
            .okHttpClient(httpClient)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

}
