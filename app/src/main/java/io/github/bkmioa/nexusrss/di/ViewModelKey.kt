package io.github.bkmioa.nexusrss.di

import android.arch.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)