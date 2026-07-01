package com.giovankov.tinytaps.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Any
)

val bottomNavItems = listOf(
    BottomNavItem("Beranda", Icons.Filled.Home, Icons.Outlined.Home, HomeRoute),
    BottomNavItem("Riwayat", Icons.Filled.History, Icons.Outlined.History, HistoryRoute),
    BottomNavItem("Pola", Icons.Filled.BarChart, Icons.Outlined.BarChart, PatternRoute),
    BottomNavItem("Pengaturan", Icons.Filled.Settings, Icons.Outlined.Settings, SettingsRoute)
)

@Composable
fun TinyTapsBottomNavBar(
    navController: NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val routeName = item.route::class.qualifiedName ?: ""
            val selected = currentDestination?.route?.contains(routeName) == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
