package io.github.bkmioa.nexusrss.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.bkmioa.nexusrss.viewmodel.MainViewModel
import io.github.bkmioa.nexusrss.viewmodel.RssListViewModel
import io.github.bkmioa.nexusrss.viewmodel.TabListViewModel
import io.github.bkmioa.nexusrss.viewmodel.ViewModelFactory


@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TabListViewModel::class)
    abstract fun bindTabListViewModel(tabListViewModel: TabListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RssListViewModel::class)
    abstract fun bindRssListViewModel(rssListViewModel: RssListViewModel): ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}