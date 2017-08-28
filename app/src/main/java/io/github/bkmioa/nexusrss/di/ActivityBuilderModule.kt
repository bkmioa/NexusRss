package io.github.bkmioa.nexusrss.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.bkmioa.nexusrss.ui.DetailActivity
import io.github.bkmioa.nexusrss.ui.MainActivity
import io.github.bkmioa.nexusrss.ui.TabListActivity


@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector
    internal abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun detailActivity(): DetailActivity

    @ContributesAndroidInjector
    internal abstract fun tabListActivity(): TabListActivity
}
