package com.coffeehub.pos.presentation.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
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

        // ─── Login ───────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ─── Dashboard ───────────────────────────────────────────────────────
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

        // ─── Menu / Products ─────────────────────────────────────────────────
        composable(Screen.Menu.route) {
            MenuScreen(
                windowSizeClass = windowSizeClass,
                onAddProduct = { navController.navigate(Screen.AddEditProduct.createRoute()) },
                onEditProduct = { id -> navController.navigate(Screen.AddEditProduct.createRoute(id)) },
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

        // ─── Orders ──────────────────────────────────────────────────────────
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

        // ─── Checkout Flow (Cashier + Payment share CartViewModel) ───────────
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
                    onNavigateToTables = { navController.navigate(Screen.Tables.route) },
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

        // ─── Tables ──────────────────────────────────────────────────────────
        // Tables is outside checkout_flow so it can be reached from Dashboard too.
        // When navigated from Cashier (Dine-In), the CartViewModel from checkout_flow
        // is retrieved via the back-stack and the selected table is stored there.
        composable(Screen.Tables.route) { backStackEntry ->
            // Try to get the checkout_flow CartViewModel if it's on the back-stack
            val checkoutEntry = remember(backStackEntry) {
                runCatching { navController.getBackStackEntry("checkout_flow") }.getOrNull()
            }
            val cartViewModel: com.coffeehub.pos.presentation.cart.CartViewModel? =
                if (checkoutEntry != null)
                    androidx.hilt.navigation.compose.hiltViewModel(checkoutEntry)
                else null

            TablesScreen(
                windowSizeClass = windowSizeClass,
                onTableSelected = { tableId, tableNumber ->
                    cartViewModel?.setTable(tableId, tableNumber)
                    navController.popBackStack()          // back to Cashier
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Customers ───────────────────────────────────────────────────────
        composable(Screen.Customers.route) {
            CustomersScreen(
                windowSizeClass = windowSizeClass,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Reports ─────────────────────────────────────────────────────────
        composable(Screen.Reports.route) {
            ReportsScreen(
                windowSizeClass = windowSizeClass,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ─── Settings ────────────────────────────────────────────────────────
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
