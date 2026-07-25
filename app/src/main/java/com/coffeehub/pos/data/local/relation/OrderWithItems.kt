package com.coffeehub.pos.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.coffeehub.pos.data.local.entity.OrderEntity
import com.coffeehub.pos.data.local.entity.OrderItemEntity

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)
