package org.creategoodthings.vault.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.creategoodthings.vault.AppContainer
import org.creategoodthings.vault.ui.pages.home.HomePage
import org.creategoodthings.vault.ui.pages.home.HomePageViewModel
import org.creategoodthings.vault.ui.pages.storage.StoragePage

@Composable
fun ApplicationNavigationHost(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    startPageRoute: PageNavigation = PageNavigation.Home
) {
    NavHost(
        navController = navController,
        startDestination = startPageRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        //Main Screen
        composable<PageNavigation.Home> { backStackEntry ->
            val viewModel = viewModel<HomePageViewModel> {
                HomePageViewModel(appContainer.productRepo, appContainer.preferencesRepository)
            }
            HomePage(
                navController = navController,
                viewModel = viewModel
            )
        }

        //Storage
        composable<PageNavigation.Storage> { backStackEntry ->
            val args = backStackEntry.toRoute<PageNavigation.Storage>()
            StoragePage(
                storageID = args.storageID,
                navController = navController
            )
        }

        //Settings
    }
}