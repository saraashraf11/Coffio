package com.coffeehub.pos.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Menu : Screen("menu")
    object Cashier : Screen("cashier")
    object Orders : Screen("orders")
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: Int) = "order_detail/$orderId"
    }
    object Payment : Screen("payment")
    object Customers : Screen("customers")
    object Tables : Screen("tables")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object AddEditProduct : Screen("add_edit_product?productId={productId}") {
        fun createRoute(productId: Int? = null) = if (productId != null)
            "add_edit_product?productId=$productId" else "add_edit_product"
    }
}
