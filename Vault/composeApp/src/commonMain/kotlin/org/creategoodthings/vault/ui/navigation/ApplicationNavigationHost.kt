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
import org.creategoodthings.vault.ui.pages.settings.SettingsPage
import org.creategoodthings.vault.ui.pages.home.HomePage
import org.creategoodthings.vault.ui.pages.home.HomePageViewModel
import org.creategoodthings.vault.ui.pages.settings.SettingsViewModel
import org.creategoodthings.vault.ui.pages.storage.StoragePage
import org.creategoodthings.vault.ui.pages.storage.StoragePageViewModel
import org.creategoodthings.vault.ui.pages.suggestionsPage.SuggestionsPage
import org.creategoodthings.vault.ui.pages.suggestionsPage.SuggestionsPageViewModel
import org.creategoodthings.vault.ui.pages.premium.LoginViewModel
import org.creategoodthings.vault.ui.pages.premium.RegisterPage

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
                HomePageViewModel(
                    appContainer.productRepo,
                    appContainer.preferencesRepository,
                    appContainer.notificationScheduler,
                    appContainer.permissionController,
                    _syncManager = appContainer.syncManager,
                    _purchaseManager = appContainer.purchaseManager,
                    _authRepo = appContainer.authRepository
                )
            }
            HomePage(
                navController = navController,
                viewModel = viewModel
            )
        }

        //Storage
        composable<PageNavigation.Storage> { backStackEntry ->
            val args = backStackEntry.toRoute<PageNavigation.Storage>()
            val viewModel = viewModel<StoragePageViewModel> {
                StoragePageViewModel(
                    args.storageID,
                        appContainer.productRepo,
                    appContainer.preferencesRepository,
                    appContainer.notificationScheduler,
                    appContainer.permissionController,
                    _syncManager = appContainer.syncManager,
                    _purchaseManager = appContainer.purchaseManager
                )
            }
            StoragePage(
                navController = navController,
                viewModel = viewModel
            )
        }

        //Settings
        composable<PageNavigation.Settings> {
            val viewModel = viewModel<SettingsViewModel> {
                SettingsViewModel(
                    appContainer.preferencesRepository
                )
            }

            SettingsPage(
                navController = navController,
                viewModel = viewModel
            )
        }

        //Suggestions
        composable<PageNavigation.Suggestions> {
            val viewModel = viewModel<SuggestionsPageViewModel> {
                SuggestionsPageViewModel(
                    appContainer.productRepo,
                    appContainer.preferencesRepository,
                    appContainer.notificationScheduler,
                    appContainer.permissionController
                )
            }
            SuggestionsPage(
                navController = navController,
                viewModel = viewModel
            )
        }

        //Register
        composable<PageNavigation.Register> {
            val viewModel = viewModel<LoginViewModel> {
                LoginViewModel(
                    _productRepo = appContainer.productRepo,
                    _authRepo = appContainer.authRepository,
                    _purchaseManager = appContainer.purchaseManager
                )
            }
            RegisterPage(viewModel = viewModel)
        }
    }
}