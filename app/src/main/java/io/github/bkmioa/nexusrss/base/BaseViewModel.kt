package io.github.bkmioa.nexusrss.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.koin.core.component.KoinComponent

open class BaseViewModel(val app: Application) : AndroidViewModel(app), KoinComponent {
}