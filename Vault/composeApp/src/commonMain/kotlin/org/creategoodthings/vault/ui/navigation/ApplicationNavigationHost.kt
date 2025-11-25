package org.creategoodthings.vault.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.creategoodthings.vault.ui.pages.HomePage
import org.creategoodthings.vault.ui.pages.PageShell

@Composable
fun ApplicationNavigationHost(
    navController: NavHostController = rememberNavController(),
    startPageRoute: String = PageNavigation.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startPageRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        //Main Screen
        composable(PageNavigation.Home.route) { backStackEntry ->
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
    }
}