package com.hadiyarajesh.compose_exoplayer.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hadiyarajesh.compose_exoplayer.ui.navigation.ComposeExoplayerNavigation
import com.hadiyarajesh.compose_exoplayer.ui.theme.ComposeExoplayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeExoplayerApp() {
    ComposeExoplayerTheme {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
            ComposeExoplayerNavigation(
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}
