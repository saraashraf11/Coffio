package com.coffeehub.pos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coffeehub.pos.domain.model.Order
import com.coffeehub.pos.domain.model.OrderStatus
import com.coffeehub.pos.utils.toFormattedDateTime
import com.coffeehub.pos.utils.toCurrencyString
import com.coffeehub.pos.presentation.theme.ErrorRed
import com.coffeehub.pos.presentation.theme.InfoBlue
import com.coffeehub.pos.presentation.theme.SuccessGreen
import com.coffeehub.pos.presentation.theme.WarningAmber

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (order.status) {
        OrderStatus.PENDING -> WarningAmber
        OrderStatus.IN_PROGRESS -> InfoBlue
        OrderStatus.READY -> SuccessGreen
        OrderStatus.COMPLETED -> MaterialTheme.colorScheme.onSurfaceVariant
        OrderStatus.CANCELLED -> ErrorRed
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.15f), MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = order.status.name.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${order.items.size} items · ${order.orderType.name.replace("_", " ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = order.createdAt.toFormattedDateTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = order.total.toCurrencyString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
