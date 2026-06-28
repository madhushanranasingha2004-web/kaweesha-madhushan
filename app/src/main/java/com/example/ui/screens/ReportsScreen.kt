package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.TransactionViewModel

@Composable
fun ReportsScreen(
    viewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()

    val totalBalance = totalIncome - totalExpenses
    val savingsRate = if (totalIncome > 0) ((totalIncome - totalExpenses) / totalIncome * 100).coerceAtLeast(0.0) else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header Section
        item {
            Column {
                Text(
                    text = "මූල්‍ය වාර්තා සහ විශ්ලේෂණය",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Financial Reports & Insights",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        // 2. High-level KPIs Row
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reports_kpi_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KpiItem(
                        icon = Icons.Default.Savings,
                        iconTint = MaterialTheme.colorScheme.primary,
                        title = "Savings Rate",
                        value = String.format("%.1f%%", savingsRate),
                        modifier = Modifier.weight(1f)
                    )
                    Divider(
                        modifier = Modifier
                            .height(50.dp)
                            .width(1.dp)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    KpiItem(
                        icon = Icons.Default.TrendingUp,
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        title = "Net Flow",
                        value = formatLKR(totalBalance),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Spending Trends Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cash Flow Summary | මුදල් ප්‍රවාහය",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress indicators representing ratios
                    val totalVolume = totalIncome + totalExpenses
                    val incomeRatio = if (totalVolume > 0) (totalIncome / totalVolume).toFloat() else 0.5f
                    val expenseRatio = if (totalVolume > 0) (totalExpenses / totalVolume).toFloat() else 0.5f

                    Text(
                        text = "Inflow vs Outflow Volume",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        if (incomeRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(incomeRatio)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            bottomStart = 12.dp,
                                            topEnd = if (expenseRatio == 0f) 12.dp else 0.dp,
                                            bottomEnd = if (expenseRatio == 0f) 12.dp else 0.dp
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = String.format("%.0f%%", incomeRatio * 100),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (expenseRatio > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(expenseRatio)
                                    .background(
                                        MaterialTheme.colorScheme.secondary,
                                        RoundedCornerShape(
                                            topEnd = 12.dp,
                                            bottomEnd = 12.dp,
                                            topStart = if (incomeRatio == 0f) 12.dp else 0.dp,
                                            bottomStart = if (incomeRatio == 0f) 12.dp else 0.dp
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = String.format("%.0f%%", expenseRatio * 100),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Total Inflow: ${formatLKR(totalIncome)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Total Outflow: ${formatLKR(totalExpenses)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 4. Detailed Category Distribution List
        item {
            Text(
                text = "Expense Distribution | වියදම් විස්තරය",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val expenseTransactions = transactions.filter { !it.isIncome }
        val categoryTotals = expenseTransactions.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }

        if (categoryTotals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No expenses recorded to analyze.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(categoryTotals) { (category, amount) ->
                val ratio = if (totalExpenses > 0) (amount / totalExpenses).toFloat() else 0.0f
                CategoryReportRow(
                    categoryName = category,
                    amount = amount,
                    ratio = ratio
                )
            }
        }
    }
}

@Composable
fun KpiItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconTint.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CategoryReportRow(
    categoryName: String,
    amount: Double,
    ratio: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (categoryName.lowercase()) {
                        "groceries" -> Icons.Default.ShoppingCart
                        "salary" -> Icons.Default.Work
                        "transport" -> Icons.Default.Commute
                        "utilities" -> Icons.Default.Bolt
                        "shopping" -> Icons.Default.ShoppingBag
                        "food" -> Icons.Default.Restaurant
                        "rent" -> Icons.Default.Home
                        else -> Icons.Default.Category
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = formatLKR(amount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(3.dp)
                    ),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = String.format("%.1f%%", ratio * 100),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
