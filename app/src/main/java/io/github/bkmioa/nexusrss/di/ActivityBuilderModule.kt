package io.github.bkmioa.nexusrss.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.bkmioa.nexusrss.ui.DetailActivity
import io.github.bkmioa.nexusrss.ui.MainActivity


@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector
    internal abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun detailActivity(): DetailActivity
}