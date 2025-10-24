package io.github.bkmioa.nexusrss.checkversion

import android.widget.Toast
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import io.github.bkmioa.nexusrss.App
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.model.Release
import io.github.bkmioa.nexusrss.repository.GithubService
import io.github.g00fy2.versioncompare.Version
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UiState(
    val localVersion: String = "0.0.0",
    val remoteVersion: Release? = null,
    val canUpgrade: Boolean = false,
    val showSnapBar: Boolean = true,
) : MavericksState {

}

class CheckVersionViewModel(initialState: UiState) : MavericksViewModel<UiState>(initialState), KoinComponent {
    private val githubService: GithubService by inject()

    init {
        val manager = App.instance.packageManager
        viewModelScope.launch(Dispatchers.IO) {
            manager.getPackageInfo(App.instance.packageName, 0)?.let {
                setState { copy(localVersion = it.versionName ?: "0.0.0") }
            }
        }
        checkVersion(true)
    }

    fun checkVersion(silent: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        if (!silent) {
            toast(App.instance.getString(R.string.checking_version))
        }
        val release = try {
            githubService.releaseList().firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            if (!silent) {
                toast(e.message ?: App.instance.getString(R.string.loading_error_toast))
            }
            return@launch
        }
        val canUpgrade = if (release == null) false else canUpgrade(awaitState().localVersion, release.tagName)

        if (!canUpgrade && !silent) {
            toast(App.instance.getString(R.string.no_new_version))
        }

        setState {
            copy(remoteVersion = release, canUpgrade = canUpgrade, showSnapBar = canUpgrade)
        }
    }

    private fun canUpgrade(localVersion: String, remoteVersion: String?): Boolean {
        remoteVersion ?: return false
        return Version(remoteVersion.trimStart('v', 'V')) > Version(localVersion)
    }

    private fun toast(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(App.instance, message, Toast.LENGTH_SHORT).show()
        }
    }


    fun setShowSnackBar(show: Boolean) {
        setState { copy(showSnapBar = show) }
    }
}