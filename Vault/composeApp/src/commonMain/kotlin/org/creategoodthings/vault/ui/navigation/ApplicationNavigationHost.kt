package org.creategoodthings.vault.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.creategoodthings.vault.ui.pages.HomePage
import org.creategoodthings.vault.ui.pages.PageShell
import org.creategoodthings.vault.ui.pages.StoragePage

@Composable
fun ApplicationNavigationHost(
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
            PageShell(
                navController,
                pageContent = { padding ->
                    HomePage(
                        modifier = Modifier.padding(padding),
                        navController = navController
                    )
                }
            )
        }

        //Storage
        composable<PageNavigation.Storage> { backStackEntry ->
            val args = backStackEntry.toRoute<PageNavigation.Storage>()
            PageShell(
                navController,
                pageContent = { padding ->
                    StoragePage(
                        storageID = args.storageID,
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                }
            )
        }

        //Settings
    }
}