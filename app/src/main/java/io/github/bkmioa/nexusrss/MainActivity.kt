package io.github.bkmioa.nexusrss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import io.github.bkmioa.nexusrss.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                AppNavHost()
            }
        }
    }
}