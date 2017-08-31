package io.github.bkmioa.nexusrss.di

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import dagger.Module
import dagger.Provides
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.BuildConfig
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.repository.JavaNetCookieJar
import io.github.bkmioa.nexusrss.repository.Service
import io.github.bkmioa.nexusrss.repository.UTorrentService
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.net.CookieManager
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {
    companion object {
        const val RSS = "rss"
        const val U_TORRENT = "uTorrent"
    }

    @Singleton
    @Provides
    fun provideService(@Named(RSS) retrofit: Retrofit): Service {
        return retrofit.create(Service::class.java)
    }

    @Singleton
    @Provides
    @Named(RSS)
    fun provideRssRetrofit(@Named(RSS) httpclient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Settings.BASE_URL)
                .client(httpclient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build()
    }

    @Singleton
    @Provides
    @Named(RSS)
    fun provideRssHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
    }

    @Singleton
    @Provides
    fun provideUTorrentService(@Named(U_TORRENT) retrofit: Retrofit): UTorrentService {
        return retrofit.create(UTorrentService::class.java)
    }


    @Provides
    @Named(U_TORRENT)
    fun provideUTorrentRetrofit(@Named(U_TORRENT) httpclient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Settings.REMOTE_URL)
                .client(httpclient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @Named(U_TORRENT)
    fun provideUTorrentHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .cookieJar(JavaNetCookieJar(CookieManager()))
                .authenticator { _, response ->
                    response.request().newBuilder()
                            .header("Authorization",
                                    Credentials.basic(Settings.REMOTE_USERNAME, Settings.REMOTE_PASSWORD))
                            .build()
                }
                .build()
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        return logging
    }

    @Singleton
    @Provides
    fun provideAppDatabase(app: App): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT INTO tab (title,options,`order`,isShow) VALUES\n" +
                                "('MOVIE','cat401,cat419,cat420,cat421,cat439',0,1),\n" +
                                "('TV','cat403,cat402,cat435,cat438',1,1),\n" +
                                "('ANIME','cat405',2,1),\n" +
                                "('MUSIC','cat406,cat408,cat434',3,1)")
                    }
                })
                .build()

    }

}