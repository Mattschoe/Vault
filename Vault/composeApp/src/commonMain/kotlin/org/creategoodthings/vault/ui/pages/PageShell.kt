package org.creategoodthings.vault.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun PageShell(
    navController: NavHostController,
    pageContent: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = { if (floatingActionButton != null) floatingActionButton() }
        ) { innerPadding ->
            pageContent(innerPadding)
        }
    }
}