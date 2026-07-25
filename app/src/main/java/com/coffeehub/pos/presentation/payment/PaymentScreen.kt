package com.coffeehub.pos.presentation.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brewpoint.pos.R
import com.coffeehub.pos.domain.model.PaymentMethod
import com.coffeehub.pos.presentation.cart.CartViewModel
import com.coffeehub.pos.presentation.theme.EspressoBrown
import com.coffeehub.pos.presentation.theme.LatteCaramel
import com.coffeehub.pos.presentation.theme.SuccessGreen
import com.coffeehub.pos.presentation.theme.WarmWhite
import com.coffeehub.pos.utils.toCurrencyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onPaymentComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    paymentViewModel: PaymentViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val paymentState by paymentViewModel.uiState.collectAsState()
    val cartState by cartViewModel.uiState.collectAsState()

    LaunchedEffect(cartState.lastOrderId) {
        if (cartState.lastOrderId != null) {
            cartViewModel.clearCart()
            onPaymentComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.payment), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order Total card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(EspressoBrown, LatteCaramel)))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.total_amount), style = MaterialTheme.typography.bodyLarge, color = WarmWhite.copy(alpha = 0.8f))
                        Text(
                            text = cartState.total.toCurrencyString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = WarmWhite
                        )
                        Text(stringResource(R.string.items_count, cartState.cartItems.size), style = MaterialTheme.typography.bodyMedium, color = WarmWhite.copy(alpha = 0.7f))
                    }
                }
            }

            // Payment Method selection
            Text(stringResource(R.string.payment_method), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PaymentMethodCard(
                    method = PaymentMethod.CASH, icon = Icons.Default.Money, label = stringResource(R.string.cash),
                    isSelected = paymentState.selectedMethod == PaymentMethod.CASH,
                    onClick = { paymentViewModel.selectPaymentMethod(PaymentMethod.CASH) },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodCard(
                    method = PaymentMethod.CARD, icon = Icons.Default.CreditCard, label = stringResource(R.string.card),
                    isSelected = paymentState.selectedMethod == PaymentMethod.CARD,
                    onClick = { paymentViewModel.selectPaymentMethod(PaymentMethod.CARD) },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodCard(
                    method = PaymentMethod.QR_CODE, icon = Icons.Default.QrCode, label = stringResource(R.string.qr),
                    isSelected = paymentState.selectedMethod == PaymentMethod.QR_CODE,
                    onClick = { paymentViewModel.selectPaymentMethod(PaymentMethod.QR_CODE) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Cash input
            if (paymentState.selectedMethod == PaymentMethod.CASH) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.cash_payment), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = paymentState.cashReceived,
                            onValueChange = { paymentViewModel.onCashReceivedChange(it, cartState.total) },
                            label = { Text(stringResource(R.string.cash_received)) },
                            leadingIcon = { Icon(Icons.Default.Money, null) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = MaterialTheme.shapes.medium
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.change), style = MaterialTheme.typography.titleSmall)
                            Text(
                                paymentState.change.toCurrencyString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }

            // Quick amounts
            if (paymentState.selectedMethod == PaymentMethod.CASH) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(20.0, 50.0, 100.0).forEach { amount ->
                        OutlinedButton(
                            onClick = { paymentViewModel.onCashReceivedChange(amount.toString(), cartState.total) },
                            modifier = Modifier.weight(1f)
                        ) { Text(amount.toCurrencyString()) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm payment
            Button(
                onClick = { paymentViewModel.processPayment(cartViewModel) },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                enabled = !paymentState.isProcessing &&
                    (paymentState.selectedMethod != PaymentMethod.CASH ||
                        (paymentState.cashReceived.toDoubleOrNull() ?: 0.0) >= cartState.total),
                shape = MaterialTheme.shapes.medium
            ) {
                if (paymentState.isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.CheckCircle, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.confirm_payment, cartState.total.toCurrencyString()), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    method: PaymentMethod,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    else MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(bgColor)
            .border(2.dp, borderColor, MaterialTheme.shapes.large)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
