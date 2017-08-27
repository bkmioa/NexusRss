package io.github.bkmioa.nexusrss.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.bkmioa.nexusrss.ui.ListFragment


@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeListFragment(): ListFragment

}