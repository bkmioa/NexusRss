package io.github.bkmioa.nexusrss

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.bkmioa.nexusrss.detail.DetailScreen
import io.github.bkmioa.nexusrss.home.HomeScreen
import io.github.bkmioa.nexusrss.search.SearchScreen
import io.github.bkmioa.nexusrss.tabs.TabsScreen

val LocalNavController = staticCompositionLocalOf<NavHostController> { error("shouldn't happen") }

object Routers {
    const val HOME = "home"
    const val SEARCH = "search"
    const val TABS = "tabs"
    const val DETAIL = "detail/{id}"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "home",
) {
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination,
            enterTransition = { fadeIn(animationSpec = tween()) },
            exitTransition = { fadeOut(animationSpec = tween()) },
        ) {
            composable(Routers.HOME) {
                HomeScreen()
            }
            composable(Routers.SEARCH) {
                SearchScreen()
            }
            composable(Routers.TABS) {
                TabsScreen()
            }
            composable(
                Routers.DETAIL,
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments!!.getString("id")!!
                DetailScreen(id)
            }
        }
    }
}
