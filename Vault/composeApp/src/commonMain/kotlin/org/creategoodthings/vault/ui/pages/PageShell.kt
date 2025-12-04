package org.creategoodthings.vault.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PageShell(
    modifier: Modifier = Modifier,
    floatingActionButton: (@Composable () -> Unit)? = null,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = { if (floatingActionButton != null) floatingActionButton() }
        ) { innerPadding ->
            val padding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding(),
                start = 24.dp,
                end = 24.dp
            )
            pageContent(padding)
        }
    }
}