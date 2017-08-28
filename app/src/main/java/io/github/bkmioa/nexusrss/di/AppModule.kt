package io.github.bkmioa.nexusrss.di

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import dagger.Module
import dagger.Provides
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.repository.Service
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideService(retrofit: Retrofit): Service {
        return retrofit.create(Service::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(httpclient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Settings.BASE_URL)
                .client(httpclient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build()
    }


    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
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