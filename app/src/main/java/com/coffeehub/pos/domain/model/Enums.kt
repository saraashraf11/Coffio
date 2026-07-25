package com.coffeehub.pos.domain.model

enum class OrderType {
    DINE_IN, TAKEAWAY
}

enum class OrderStatus {
    PENDING, IN_PROGRESS, READY, COMPLETED, CANCELLED
}

enum class PaymentMethod {
    CASH, CARD, QR_CODE, MIXED
}

enum class UserRole {
    MANAGER, CASHIER, BARISTA
}

enum class TableStatus {
    AVAILABLE, OCCUPIED, RESERVED, CLEANING
}

enum class ProductSize(val label: String, val priceMultiplier: Double) {
    SMALL("S", 0.9),
    MEDIUM("M", 1.0),
    LARGE("L", 1.2)
}

enum class Temperature(val label: String) {
    HOT("Hot"),
    ICED("Iced"),
    BLENDED("Blended")
}

enum class MilkType(val label: String) {
    WHOLE("Whole Milk"),
    OAT("Oat Milk"),
    ALMOND("Almond Milk"),
    SOY("Soy Milk"),
    COCONUT("Coconut Milk"),
    NONE("No Milk")
}
