package io.github.bkmioa.nexusrss

import android.os.Bundle
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.bkmioa.nexusrss.detail.DetailScreen
import io.github.bkmioa.nexusrss.home.HomeScreen
import io.github.bkmioa.nexusrss.model.Item
import io.github.bkmioa.nexusrss.search.SearchScreen
import io.github.bkmioa.nexusrss.settings.SettingsScreen
import io.github.bkmioa.nexusrss.tabs.TabsScreen

val LocalNavController = staticCompositionLocalOf<NavHostController> { error("shouldn't happen") }

sealed class Router(val route: String) {
    fun navigate(
        navController: NavHostController,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        navController.navigate(this, args, navOptions, navigatorExtras)
    }

    object Home : Router("home")
    object Search : Router("search")
    object Tabs : Router("tabs")
    object Detail : Router("detail/{id}") {
        fun navigate(navController: NavHostController, id: String, item: Item? = null) {
            val args = Bundle().apply {
                putString("id", id)
                item?.let { putParcelable("data", it) }
            }
            navigate(navController, args)
        }
    }

    object Settings : Router("settings")
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Router.Home.route,
) {
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination,
            enterTransition = { fadeIn(animationSpec = tween()) },
            exitTransition = { fadeOut(animationSpec = tween()) },
        ) {
            composable(Router.Home.route) {
                HomeScreen()
            }
            composable(Router.Search.route) {
                SearchScreen()
            }
            composable(Router.Tabs.route) {
                TabsScreen()
            }
            composable(
                route = Router.Detail.route,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                        nullable = false
                    },
                    navArgument("data") {
                        type = NavType.ParcelableType(Item::class.java)
                        nullable = true
                    },
                ),
            ) { backStackEntry ->
                val args = checkNotNull(backStackEntry.arguments)
                DetailScreen(args)
            }
            composable(Router.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

fun NavController.navigate(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val nodeId = graph.findNode(route = route)?.id
    if (nodeId != null) {
        navigate(nodeId, args, navOptions, navigatorExtras)
    }
}

fun NavHostController.navigate(
    route: Router,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    if (args != null) {
        navigate(route.route, args, navOptions, navigatorExtras)
    } else {
        navigate(route.route, navOptions, navigatorExtras)
    }
}