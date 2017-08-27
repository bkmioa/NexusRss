package io.github.bkmioa.nexusrss.di

import dagger.Module
import dagger.Provides
import io.github.bkmioa.nexusrss.Settings
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

}