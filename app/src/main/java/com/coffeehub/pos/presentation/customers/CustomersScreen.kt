package com.coffeehub.pos.presentation.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brewpoint.pos.R
import com.coffeehub.pos.domain.model.Customer
import com.coffeehub.pos.presentation.components.EmptyState
import com.coffeehub.pos.presentation.theme.SuccessGreen
import com.coffeehub.pos.utils.toCurrencyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateBack: () -> Unit,
    viewModel: CustomersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.customers), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.add_customer)) },
                icon = { Icon(Icons.Default.PersonAdd, null) },
                onClick = viewModel::showAddDialog
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text(stringResource(R.string.search_customers)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true
            )
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (uiState.customers.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.People,
                    title = stringResource(R.string.no_customers_title),
                    subtitle = stringResource(R.string.no_customers_subtitle),
                    actionLabel = stringResource(R.string.add_customer),
                    onAction = viewModel::showAddDialog,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(uiState.customers) { customer ->
                        CustomerCard(customer = customer, onEdit = { viewModel.showEditDialog(customer) },
                            onDelete = { viewModel.deleteCustomer(customer) })
                    }
                }
            }
        }
        if (uiState.showAddDialog) {
            AddEditCustomerDialog(customer = uiState.editingCustomer, onSave = viewModel::saveCustomer, onDismiss = viewModel::hideDialog)
        }
    }
}

@Composable
private fun CustomerCard(customer: Customer, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) {
                    Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                        Text(text = customer.name.take(1).uppercase(), style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(customer.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                if (customer.phone.isNotEmpty()) {
                    Text(customer.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = SuccessGreen)
                    Text(" ${customer.loyaltyPoints} ${stringResource(R.string.pts, customer.loyaltyPoints).substringAfter(" ")}",
                        style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                    Text(" · ${customer.totalSpend.toCurrencyString()} ${stringResource(R.string.total_spend, "").trim()}",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit), modifier = Modifier.size(20.dp)) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun AddEditCustomerDialog(customer: Customer?, onSave: (String, String, String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(customer?.name ?: "") }
    var phone by remember { mutableStateOf(customer?.phone ?: "") }
    var email by remember { mutableStateOf(customer?.email ?: "") }
    var notes by remember { mutableStateOf(customer?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (customer != null) stringResource(R.string.edit_customer) else stringResource(R.string.new_customer), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.name_required)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.phone)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.email)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(stringResource(R.string.notes)) }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onSave(name, phone, email, notes) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
