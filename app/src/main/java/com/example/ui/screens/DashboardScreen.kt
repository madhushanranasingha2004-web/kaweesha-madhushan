package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Transaction
import com.example.ui.TransactionViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: TransactionViewModel,
    onNavigateToAddTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpenses by viewModel.totalExpenses.collectAsStateWithLifecycle()
    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Summary Cards Row
        item {
            SummaryCardsSection(
                totalIncome = totalIncome,
                totalExpenses = totalExpenses,
                currentBalance = currentBalance
            )
        }

        // 2. Charts Section
        item {
            ChartsSection(
                totalIncome = totalIncome,
                totalExpenses = totalExpenses
            )
        }

        // 3. Recent Transactions Title & Filter Section
        item {
            RecentTransactionsHeader(
                onExportClick = { /* No-op, visual only */ }
            )
        }

        // 4. Recent Transactions Table List
        if (transactions.isEmpty()) {
            item {
                EmptyStateCard()
            }
        } else {
            items(transactions.take(10)) { tx ->
                TransactionRowItem(
                    transaction = tx,
                    onDeleteClick = { viewModel.deleteTransaction(tx.id) }
                )
            }
        }

        // 5. Paging Footer Section
        item {
            TransactionsTableFooter(
                totalCount = transactions.size
            )
        }
    }
}

@Composable
fun SummaryCardsSection(
    totalIncome: Double,
    totalExpenses: Double,
    currentBalance: Double
) {
    val isCompact = false // Can adapt based on LocalConfiguration if needed
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Total Balance Card (Vibrant Lavender Summary Card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_total_balance"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                Column {
                    Text(
                        text = "Total Balance | මුළු ශේෂය",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatLKR(currentBalance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Available wealth physically secured & tracked",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.1f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total Income Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("card_total_income"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Column {
                        Text(
                            text = "Total Income | මුළු ආදායම",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatLKR(totalIncome),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+12.5%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "vs last month",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            // Total Expenses Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("card_total_expenses"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Column {
                        Text(
                            text = "Total Expenses | මුළු වියදම",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatLKR(totalExpenses),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "-5.2%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "vs last month",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }

        // Savings Goal Card (Span Full Width)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_savings_goal"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Savings Goals | ඉතුරුම් ඉලක්ක",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LKR 450,000",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Linear Progress Bar
                    val progress = 0.65f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "65% of 2024 Travel Goal reached",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
fun ChartsSection(
    totalIncome: Double,
    totalExpenses: Double
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Income vs Expenses Bar Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Income vs Expenses | ආදායම සහ වියදම",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Financial activity over the last 6 months",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = "Last 6 Months",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Custom Bar Chart Representation
                val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                // Define heights (in dp) for mock bars
                val incomeHeights = listOf(140.dp, 170.dp, 120.dp, 180.dp, 150.dp, 160.dp)
                val expenseHeights = listOf(90.dp, 100.dp, 130.dp, 80.dp, 70.dp, 110.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    months.forEachIndexed { index, month ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Income Bar (Teal/Emerald)
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .height(incomeHeights[index])
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )
                                // Expense Bar (Coral Red)
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .height(expenseHeights[index])
                                        .background(
                                            MaterialTheme.colorScheme.secondary,
                                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = month,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Income (ආදායම)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Expenses (වියදම)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Expenses Breakdown doughnut card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Expenses Breakdown | වියදම් වර්ගීකරණය",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Doughnut Chart using canvas
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.secondary
                    val tertiaryColor = MaterialTheme.colorScheme.tertiary
                    val outlineVariantColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            // 42% Housing (Coral/Secondary), 28% Food (Tertiary), 15% Transport (Primary), 15% Other (OutlineVariant)
                            val strokeWidth = 14.dp.toPx()
                            
                            // Housing (151 degrees)
                            drawArc(
                                color = secondaryColor,
                                startAngle = -90f,
                                sweepAngle = 151.2f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            // Food (101 degrees)
                            drawArc(
                                color = tertiaryColor,
                                startAngle = 61.2f,
                                sweepAngle = 100.8f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            // Transport (54 degrees)
                            drawArc(
                                color = primaryColor,
                                startAngle = 162f,
                                sweepAngle = 54f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            // Other (54 degrees)
                            drawArc(
                                color = outlineVariantColor,
                                startAngle = 216f,
                                sweepAngle = 54f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "70%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "SPENT",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Legend values
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LegendItem(color = secondaryColor, label = "Housing | නිවාස", percent = "42%")
                        LegendItem(color = tertiaryColor, label = "Food | ආහාර", percent = "28%")
                        LegendItem(color = primaryColor, label = "Transport | ප්‍රවාහනය", percent = "15%")
                        LegendItem(color = outlineVariantColor, label = "Other | වෙනත්", percent = "15%")
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, percent: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = percent,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RecentTransactionsHeader(
    onExportClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Recent Transactions | මෑතකාලීන ගනුදෙනු",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your latest financial activity records",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Button(
            onClick = onExportClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Export Report", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun TransactionRowItem(
    transaction: Transaction,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_row_${transaction.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category Icon
                val icon = when (transaction.category.lowercase()) {
                    "groceries" -> Icons.Default.ShoppingCart
                    "salary" -> Icons.Default.Work
                    "transport" -> Icons.Default.Commute
                    "utilities" -> Icons.Default.Bolt
                    "shopping" -> Icons.Default.ShoppingBag
                    "food" -> Icons.Default.Restaurant
                    "rent" -> Icons.Default.Home
                    else -> Icons.Default.Category
                }

                val iconBgColor = if (transaction.isIncome) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                } else {
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                }

                val iconTint = if (transaction.isIncome) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBgColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.category,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Date & Category Info
                Column {
                    Text(
                        text = "${transaction.category} | ${transaction.categorySinhala}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    if (transaction.note.isNotEmpty()) {
                        Text(
                            text = transaction.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Amount, Currency & Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    val amountText = if (transaction.isIncome) {
                        "+ ${transaction.currency} ${formatAmount(transaction.amount)}"
                    } else {
                        "- ${transaction.currency} ${formatAmount(transaction.amount)}"
                    }

                    val color = if (transaction.isIncome) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }

                    Text(
                        text = amountText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    StatusChip(status = transaction.status)
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete transaction",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "COMPLETED" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        "PROCESSING" -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        else -> Color(0xFFFEE2E2) to Color(0xFFB91C1C) // FAILED
    }

    Text(
        text = status,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = Modifier
            .background(bgColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No transactions found | ගනුදෙනු කිසිවක් හමු නොවීය",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Try adding a transaction or clearing your search filter.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TransactionsTableFooter(totalCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Showing 5 of $totalCount transactions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Page Left */ },
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous page",
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "1",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .wrapContentSize(Alignment.Center)
            )
            Text(
                text = "2",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), CircleShape)
                    .clickable { }
                    .wrapContentSize(Alignment.Center)
            )
            Text(
                text = "3",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), CircleShape)
                    .clickable { }
                    .wrapContentSize(Alignment.Center)
            )
            IconButton(
                onClick = { /* Page Right */ },
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next page",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Helpers
fun formatLKR(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
    // Custom LKR currency code formatting for "LKR 123,456.00"
    return "LKR " + NumberFormat.getNumberInstance(Locale.US).format(amount)
}

fun formatAmount(amount: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).format(amount)
}
