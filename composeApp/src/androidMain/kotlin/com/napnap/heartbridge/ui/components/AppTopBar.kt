@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.heartbridge.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.napnap.heartbridge.ui.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings

@Composable
fun AppTopBar( navController: NavHostController) {
    val route = navController.currentBackStackEntryAsState().value?.destination?.route
    TopAppBar(
        modifier = Modifier.height(100.dp),
        title = {
            Text(
                "",
                modifier = Modifier.padding(top = 5.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon = {

            if(route != Screen.Main.route){
                BarButton(
                    click = {
                        navController.navigate(Screen.Main.route)
                    },
                    icon = Icons.AutoMirrored.Filled.ArrowBack, ""
                )
            }else {
                BarButton(
                    click = {
                        navController.navigate(Screen.History.route)
                    },
                    icon = Icons.Default.History, ""
                )
            }

            
        },
        actions = {
            if(route != Screen.Settings.route){
                BarButton(
                    click = {
                        navController.navigate(Screen.Settings.route)
                    },
                    icon = Icons.Filled.Settings, ""
                )
            }
        }
    )
}