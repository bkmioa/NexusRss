package io.github.bkmioa.nexusrss.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.BuildConfig
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.cookie.SharedCookieJar
import io.github.bkmioa.nexusrss.db.AppDao
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.db.DownloadDao
import io.github.bkmioa.nexusrss.repository.GithubService
import io.github.bkmioa.nexusrss.repository.JavaNetCookieJar
import io.github.bkmioa.nexusrss.repository.Service
import io.github.bkmioa.nexusrss.repository.UserAgentInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { get<Application>() as App }
    single { provideLoggingInterceptor() }
    single { provideCommonHttpClient(get(), get()) }
    single { provideService(httpclient = get()) }
    single { provideGithubService(get()) }
    single { provideAppDatabase(get()) }
    single { provideAppDao(get()) }
    single { provideDownloadDao(get()) }
}

private fun provideService(httpclient: OkHttpClient): Service {
    return Retrofit.Builder()
            .baseUrl(Settings.BASE_URL)
            .client(httpclient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JspoonConverterFactory.create())
            .build()
            .create(Service::class.java)
}

private fun provideCommonHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, app: App): OkHttpClient {
    return OkHttpClient.Builder()
            .followRedirects(false)
            .cookieJar(JavaNetCookieJar(SharedCookieJar()))
            .addInterceptor(UserAgentInterceptor(app))
            .addInterceptor(httpLoggingInterceptor)
            .build()
}

private fun provideGithubService(httpclient: OkHttpClient): GithubService {
    val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    return Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .client(httpclient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GithubService::class.java)
}

private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val logging = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
        logging.level = HttpLoggingInterceptor.Level.BODY
    }
    return logging
}

private fun provideAppDatabase(app: App): AppDatabase {
    return Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.DB_NAME)
            .allowMainThreadQueries()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    AppDatabase.initData(db)
                }
            })
            .addMigrations(*AppDatabase.migrations())
            .build()
}

private fun provideAppDao(database: AppDatabase): AppDao = database.appDao()

private fun provideDownloadDao(database: AppDatabase): DownloadDao = database.downloadDao()

