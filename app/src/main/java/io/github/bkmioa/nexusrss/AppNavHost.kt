@file:OptIn(ExperimentalSharedTransitionApi::class)

package io.github.bkmioa.nexusrss

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.require
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper

val LocalNavController = staticCompositionLocalOf<NavHostController> { error("shouldn't happen") }

object DefaultTransitions : NavHostAnimatedDestinationStyle() {
    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(animationSpec = tween())
    }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(animationSpec = tween())
    }
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    CompositionLocalProvider(LocalNavController provides navController) {
        SharedTransitionLayout {
            DestinationsNavHost(
                modifier = modifier,
                navGraph = NavGraphs.root,
                navController = navController,
                defaultTransitions = DefaultTransitions,
                dependenciesContainerBuilder = {
                    dependency(this@SharedTransitionLayout)
                },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
object SharedTransitionDataWrapper : DestinationWrapper {
    @Composable
    override fun <T> DestinationScope<T>.Wrap(screenContent: @Composable () -> Unit) {
        val sharedTransitionScope = buildDependencies().require<SharedTransitionScope>()
        val sharedTransitionData = remember {
            (this as? AnimatedVisibilityScope)?.let {
                SharedTransitionData(
                    animatedVisibilityScope = it,
                    sharedTransitionScope = sharedTransitionScope
                )
            }
        }
        CompositionLocalProvider(LocalSharedTransitionData provides sharedTransitionData) {
            screenContent()
        }
    }
}