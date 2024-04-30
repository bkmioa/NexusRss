package io.github.bkmioa.nexusrss

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.bkmioa.nexusrss.home.HomeScreen
import io.github.bkmioa.nexusrss.search.SearchScreen
import io.github.bkmioa.nexusrss.tabs.TabsScreen

object Routers {
    const val HOME = "home"
    const val SEARCH = "search"
    const val TABS = "tabs"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routers.HOME,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween()) },
        exitTransition = { fadeOut(animationSpec = tween()) },
    ) {
        composable(Routers.HOME) {
            HomeScreen(navController)
        }
        composable(Routers.SEARCH) {
            SearchScreen(navController)
        }
        composable(Routers.TABS) {
            TabsScreen(navController)
        }
    }
}
