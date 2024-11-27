package com.renanfran.transactionapp.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.renanfran.transactionapp.android.feature.add_transaction.AddTransaction
import com.renanfran.transactionapp.android.feature.home.HomeScreen
import com.renanfran.transactionapp.android.feature.images.ImagesScreen
import com.renanfran.transactionapp.android.feature.transactionlist.TransactionListScreen
import com.renanfran.transactionapp.android.ui.theme.Zinc
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun NavHostScreen() {
    val navController = rememberNavController()
    var bottomBarVisibility by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = bottomBarVisibility) {
                NavigationBottomBar(
                    navController = navController,
                    items = listOf(
                        NavItem(route = "/home", icon = Icons.Filled.Home),
                        NavItem(route = "/saved_images", icon = Icons.Filled.FavoriteBorder)
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "/home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "/home") {
                bottomBarVisibility = true
                HomeScreen(navController)
            }

            composable(route = "/add_income") {
                bottomBarVisibility = false
                AddTransaction(navController = navController, isIncome = true)
            }

            composable(route = "/add_exp") {
                bottomBarVisibility = false
                AddTransaction(navController = navController, isIncome = false)
            }

            composable(route = "/saved_images") {
                bottomBarVisibility = true
                ImagesScreen(navController = navController)
            }

            composable(route = "/all_transactions") {
                bottomBarVisibility = true
                TransactionListScreen(navController = navController)
            }

            composable(
                route = "/edit_transaction/{transactionId}",
                arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
                bottomBarVisibility = false
                AddTransaction(
                    navController = navController,
                    isIncome = false,
                    transactionId = transactionId
                )
            }
        }
    }
}

data class NavItem(
    val route: String,
    val icon: ImageVector
)

@Composable
fun NavigationBottomBar(
    navController: NavController,
    items: List<NavItem>
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    BottomAppBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Zinc,
                    selectedIconColor = Zinc,
                    unselectedTextColor = Color.Gray,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}

