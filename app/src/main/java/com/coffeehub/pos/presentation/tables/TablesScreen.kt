package com.coffeehub.pos.presentation.tables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brewpoint.pos.R
import com.coffeehub.pos.domain.model.CoffeeTable
import com.coffeehub.pos.domain.model.TableStatus
import com.coffeehub.pos.utils.isExpanded
import com.coffeehub.pos.presentation.theme.TableAvailable
import com.coffeehub.pos.presentation.theme.TableCleaning
import com.coffeehub.pos.presentation.theme.TableOccupied
import com.coffeehub.pos.presentation.theme.TableReserved

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
    windowSizeClass: WindowSizeClass,
    onTableSelected: (Int, Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TablesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val columns = if (windowSizeClass.isExpanded()) 5 else 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tables), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                LegendItem(color = TableAvailable, label = stringResource(R.string.available))
                LegendItem(color = TableOccupied, label = stringResource(R.string.occupied))
                LegendItem(color = TableReserved, label = stringResource(R.string.reserved))
                LegendItem(color = TableCleaning, label = stringResource(R.string.cleaning))
            }
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.tables) { table ->
                        TableCell(table = table, isSelected = uiState.selectedTableId == table.id, onClick = {
                            viewModel.selectTable(table.id)
                            if (table.status == TableStatus.AVAILABLE) onTableSelected(table.id, table.tableNumber)
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(table: CoffeeTable, isSelected: Boolean, onClick: () -> Unit) {
    val statusColor = when (table.status) {
        TableStatus.AVAILABLE -> TableAvailable
        TableStatus.OCCUPIED -> TableOccupied
        TableStatus.RESERVED -> TableReserved
        TableStatus.CLEANING -> TableCleaning
    }
    Box(
        modifier = Modifier.aspectRatio(1f).clip(MaterialTheme.shapes.large)
            .background(statusColor.copy(alpha = if (isSelected) 0.35f else 0.15f))
            .border(width = if (isSelected) 2.dp else 1.dp, color = statusColor, shape = MaterialTheme.shapes.large)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.TableBar, contentDescription = null, modifier = Modifier.size(32.dp), tint = statusColor)
            Text(text = stringResource(R.string.table_number, table.tableNumber),
                style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = statusColor, textAlign = TextAlign.Center)
            Text(text = stringResource(R.string.seats, table.capacity),
                style = MaterialTheme.typography.labelSmall, color = statusColor.copy(alpha = 0.7f))
            Text(text = table.section, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(MaterialTheme.shapes.extraSmall).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
