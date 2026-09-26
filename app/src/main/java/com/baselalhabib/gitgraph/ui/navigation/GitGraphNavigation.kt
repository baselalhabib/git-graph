package com.baselalhabib.gitgraph.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.baselalhabib.gitgraph.data.model.ProviderType
import com.baselalhabib.gitgraph.ui.screens.detail.RepoDetailScreen
import com.baselalhabib.gitgraph.ui.screens.detail.RepoDetailViewModel
import com.baselalhabib.gitgraph.ui.screens.home.HomeScreen
import com.baselalhabib.gitgraph.ui.screens.home.HomeViewModel
import com.baselalhabib.gitgraph.ui.screens.settings.SettingsScreen
import com.baselalhabib.gitgraph.ui.screens.settings.SettingsViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object RepoDetail : Screen("repo_detail/{owner}/{repoName}/{repoId}/{provider}") {
        fun createRoute(owner: String, repoName: String, repoId: String, provider: ProviderType): String {
            return "repo_detail/$owner/$repoName/$repoId/${provider.name}"
        }
    }
}

@Composable
fun GitGraphNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToRepoDetail = { owner, repoName, repoId, provider ->
                    navController.navigate(Screen.RepoDetail.createRoute(owner, repoName, repoId, provider))
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.RepoDetail.route,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("repoName") { type = NavType.StringType },
                navArgument("repoId") { type = NavType.StringType },
                navArgument("provider") { type = NavType.StringType }
            )
        ) {
            val viewModel: RepoDetailViewModel = hiltViewModel()
            RepoDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
