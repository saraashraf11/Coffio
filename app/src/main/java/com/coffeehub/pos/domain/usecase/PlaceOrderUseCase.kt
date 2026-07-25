package com.coffeehub.pos.domain.usecase

import com.coffeehub.pos.domain.model.CartItem
import com.coffeehub.pos.domain.model.Order
import com.coffeehub.pos.domain.model.OrderItem
import com.coffeehub.pos.domain.model.OrderStatus
import com.coffeehub.pos.domain.model.OrderType
import com.coffeehub.pos.domain.model.PaymentMethod
import com.coffeehub.pos.domain.model.TableStatus
import com.coffeehub.pos.domain.repository.CustomerRepository
import com.coffeehub.pos.domain.repository.OrderRepository
import com.coffeehub.pos.domain.repository.TableRepository
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val tableRepository: TableRepository,
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(
        cartItems: List<CartItem>,
        orderType: OrderType,
        tableId: Int?,
        customerId: Int?,
        paymentMethod: PaymentMethod,
        cashierName: String,
        taxRate: Double = 0.14,
        discount: Double = 0.0,
        cashReceived: Double = 0.0,
        notes: String = ""
    ): Result<Long> {
        return try {
            if (cartItems.isEmpty()) {
                return Result.failure(IllegalArgumentException("Cart is empty"))
            }

            val subtotal = cartItems.sumOf { it.subtotal }
            val tax = subtotal * taxRate
            val total = subtotal + tax - discount
            val change = if (paymentMethod == PaymentMethod.CASH) cashReceived - total else 0.0

            val orderItems = cartItems.map { cartItem ->
                OrderItem(
                    productId = cartItem.product.id,
                    productName = cartItem.product.name,
                    selectedSize = cartItem.selectedSize.label,
                    selectedTemperature = cartItem.selectedTemperature.label,
                    selectedMilkType = cartItem.selectedMilkType.label,
                    extraShots = cartItem.extraShots,
                    quantity = cartItem.quantity,
                    unitPrice = cartItem.unitPrice,
                    notes = cartItem.notes
                )
            }

            val orderCount = orderRepository.getTodayOrderCount()
            val orderNumber = "BP-${String.format("%04d", orderCount + 1)}"

            val order = Order(
                orderNumber = orderNumber,
                orderType = orderType,
                tableId = tableId,
                customerId = customerId,
                items = orderItems,
                subtotal = subtotal,
                taxRate = taxRate,
                tax = tax,
                discount = discount,
                total = total,
                status = OrderStatus.PENDING,
                paymentMethod = paymentMethod,
                cashierName = cashierName,
                cashReceived = cashReceived,
                change = change,
                notes = notes
            )

            val orderId = orderRepository.insertOrder(order)

            // Update table status if dine-in
            if (orderType == OrderType.DINE_IN && tableId != null) {
                tableRepository.updateTableStatus(tableId, TableStatus.OCCUPIED, orderId.toInt())
            }

            // Add loyalty points if customer exists
            if (customerId != null) {
                val points = (total / 10).toInt()
                customerRepository.addLoyaltyPoints(customerId, points)
            }

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
