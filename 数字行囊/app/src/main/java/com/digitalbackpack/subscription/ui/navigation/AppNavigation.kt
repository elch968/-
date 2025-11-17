package com.digitalbackpack.subscription.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.digitalbackpack.subscription.ui.screens.add_edit.AddEditSubscriptionScreen
import com.digitalbackpack.subscription.ui.screens.detail.SubscriptionDetailScreen
import com.digitalbackpack.subscription.ui.screens.home.HomeScreen

/**
 * 应用导航
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // 主页
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { subscriptionId ->
                    navController.navigate(Screen.Detail.createRoute(subscriptionId))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.AddEdit.route)
                }
            )
        }
        
        // 添加/编辑订阅
        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(
                navArgument("subscriptionId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            val subscriptionId = it.arguments?.getLong("subscriptionId") ?: -1L
            AddEditSubscriptionScreen(
                subscriptionId = subscriptionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // 订阅详情
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("subscriptionId") {
                    type = NavType.LongType
                }
            )
        ) {
            val subscriptionId = it.arguments?.getLong("subscriptionId") ?: return@composable
            SubscriptionDetailScreen(
                subscriptionId = subscriptionId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.AddEdit.createRoute(id))
                }
            )
        }
    }
}

/**
 * 导航路由
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    
    object AddEdit : Screen("add_edit?subscriptionId={subscriptionId}") {
        fun createRoute(subscriptionId: Long = -1L) = "add_edit?subscriptionId=$subscriptionId"
    }
    
    object Detail : Screen("detail/{subscriptionId}") {
        fun createRoute(subscriptionId: Long) = "detail/$subscriptionId"
    }
}

