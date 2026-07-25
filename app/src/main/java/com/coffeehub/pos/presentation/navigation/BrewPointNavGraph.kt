package com.coffeehub.pos.presentation.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.navArgument
import com.coffeehub.pos.presentation.auth.LoginScreen
import com.coffeehub.pos.presentation.cashier.CashierScreen
import com.coffeehub.pos.presentation.customers.CustomersScreen
import com.coffeehub.pos.presentation.dashboard.DashboardScreen
import com.coffeehub.pos.presentation.menu.AddEditProductScreen
import com.coffeehub.pos.presentation.menu.MenuScreen
import com.coffeehub.pos.presentation.orders.OrderDetailScreen
import com.coffeehub.pos.presentation.orders.OrdersScreen
import com.coffeehub.pos.presentation.payment.PaymentScreen
import com.coffeehub.pos.presentation.reports.ReportsScreen
import com.coffeehub.pos.presentation.settings.SettingsScreen
import com.coffeehub.pos.presentation.tables.TablesScreen

@Composable
fun BrewPointNavGraph(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
                DashboardScreen(
                    windowSizeClass = windowSizeClass,
                    onNavigateTo = { route -> navController.navigate(route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Menu.route) {
                MenuScreen(
                    windowSizeClass = windowSizeClass,
                    onAddProduct = { navController.navigate(Screen.AddEditProduct.createRoute()) },
                    onEditProduct = { id ->
                        navController.navigate(
                            Screen.AddEditProduct.createRoute(
                                id
                            )
                        )
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AddEditProduct.route,
                arguments = listOf(navArgument("productId") {
                    type = NavType.IntType; defaultValue = -1
                })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId")?.takeIf { it != -1 }
                AddEditProductScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Orders.route) {
                OrdersScreen(
                    windowSizeClass = windowSizeClass,
                    onOrderClick = { id -> navController.navigate(Screen.OrderDetail.createRoute(id)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
                OrderDetailScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            navigation(startDestination = Screen.Cashier.route, route = "checkout_flow") {
                composable(Screen.Cashier.route) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("checkout_flow")
                    }
                    val cartViewModel: com.coffeehub.pos.presentation.cart.CartViewModel =
                        androidx.hilt.navigation.compose.hiltViewModel(parentEntry)

                    CashierScreen(
                        windowSizeClass = windowSizeClass,
                        onNavigateToPayment = { navController.navigate(Screen.Payment.route) },
                        onNavigateBack = { navController.popBackStack() },
                        cartViewModel = cartViewModel
                    )
                }

                composable(Screen.Payment.route) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("checkout_flow")
                    }
                    val cartViewModel: com.coffeehub.pos.presentation.cart.CartViewModel =
                        androidx.hilt.navigation.compose.hiltViewModel(parentEntry)

                    PaymentScreen(
                        onPaymentComplete = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo("checkout_flow") { inclusive = true }
                            }
                        },
                        onNavigateBack = { navController.popBackStack() },
                        cartViewModel = cartViewModel
                    )
                }
            }

            composable(Screen.Customers.route) {
                CustomersScreen(
                    windowSizeClass = windowSizeClass,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Tables.route) {
                TablesScreen(
                    windowSizeClass = windowSizeClass,
                    onTableSelected = { navController.navigate(Screen.Cashier.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Reports.route) {
                ReportsScreen(
                    windowSizeClass = windowSizeClass,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
