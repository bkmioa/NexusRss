package io.github.bkmioa.nexusrss.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.bkmioa.nexusrss.App
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuilderModule::class,
        FragmentBuildersModule::class,
        ViewModelModule::class
))
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}