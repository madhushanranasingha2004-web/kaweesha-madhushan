package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PREDEFINED_CATEGORIES
import com.example.ui.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form inputs state
    var dateString by remember { 
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        mutableStateOf(today)
    }
    var amountString by remember { mutableStateOf("") }
    var noteString by remember { mutableStateOf("") }
    
    // Dropdown selectors states
    var selectedCategoryIndex by remember { mutableStateOf(-1) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val currencies = listOf("LKR", "USD", "EUR")
    var selectedCurrencyIndex by remember { mutableStateOf(0) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    // Segmented Control for Income vs Expense
    // We can auto-select based on category, but allow overriding
    var isIncomeSelected by remember { mutableStateOf(false) }

    // Form validation state
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Success feedback animation simulated
    var isSaving by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Title section
        Column {
            Text(
                text = "නව ගනුදෙනුවක් එක් කරන්න",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add a New Transaction",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        // 2. Main Bento Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Status Chip Visual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction Type",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // "New Entry" Badge
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "නව ඇතුළත් කිරීම (New Entry)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Income / Expense Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = { isIncomeSelected = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isIncomeSelected) MaterialTheme.colorScheme.error else Color.Transparent,
                            contentColor = if (!isIncomeSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null
                    ) {
                        Text("Expense / වියදම", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { isIncomeSelected = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isIncomeSelected) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                            contentColor = if (isIncomeSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = null
                    ) {
                        Text("Income / ආදායම", fontWeight = FontWeight.Bold)
                    }
                }

                // Date Picker Input
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("දිනය", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text("Date", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_date"),
                        trailingIcon = {
                            IconButton(onClick = {
                                val calendar = Calendar.getInstance()
                                val dateParts = dateString.split("-")
                                if (dateParts.size == 3) {
                                    calendar.set(Calendar.YEAR, dateParts[0].toInt())
                                    calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1)
                                    calendar.set(Calendar.DAY_OF_MONTH, dateParts[2].toInt())
                                }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        dateString = String.format(Locale.US, "%d-%02d-%02d", year, month + 1, dayOfMonth)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Category Dropdown Input
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("වර්ගය", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text("Category", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (selectedCategoryIndex >= 0) PREDEFINED_CATEGORIES[selectedCategoryIndex].displayName else "Select category...",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("input_category"),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            PREDEFINED_CATEGORIES.forEachIndexed { index, category ->
                                DropdownMenuItem(
                                    text = { Text(category.displayName) },
                                    onClick = {
                                        selectedCategoryIndex = index
                                        categoryDropdownExpanded = false
                                        // Auto-select type based on typical default
                                        isIncomeSelected = category.defaultIsIncome
                                    }
                                )
                            }
                        }
                    }
                }

                // Amount Input
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("මුදල", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text("Amount", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amountString,
                        onValueChange = { amountString = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_amount"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Default.Payments, contentDescription = null)
                        },
                        placeholder = { Text("0.00") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Currency Dropdown Input
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("මුදල් ඒකකය", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text("Currency", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = currencyDropdownExpanded,
                        onExpandedChange = { currencyDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = currencies[selectedCurrencyIndex],
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("input_currency"),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = currencyDropdownExpanded,
                            onDismissRequest = { currencyDropdownExpanded = false }
                        ) {
                            currencies.forEachIndexed { index, currency ->
                                DropdownMenuItem(
                                    text = { Text(currency) },
                                    onClick = {
                                        selectedCurrencyIndex = index
                                        currencyDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Note Textarea
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("සටහන", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text("Note", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteString,
                        onValueChange = { noteString = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("input_note"),
                        placeholder = { Text("වැඩි විස්තර මෙහි සඳහන් කරන්න...") },
                        trailingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Error message display
                if (showError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Form Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("button_cancel"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Text(
                            text = "අවලංගු කරන්න (Cancel)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Save Button
                    Button(
                        onClick = {
                            // Validation
                            val amt = amountString.toDoubleOrNull()
                            if (selectedCategoryIndex < 0) {
                                showError = true
                                errorMessage = "කරුණාකර වර්ගයක් තෝරන්න (Please select a category)"
                            } else if (amt == null || amt <= 0.0) {
                                showError = true
                                errorMessage = "කරුණාකර වලංගු මුදලක් ඇතුළත් කරන්න (Please enter a valid amount)"
                            } else {
                                showError = false
                                isSaving = true
                                
                                val cat = PREDEFINED_CATEGORIES[selectedCategoryIndex]
                                viewModel.addTransaction(
                                    date = dateString,
                                    category = cat.nameEn,
                                    categorySinhala = cat.nameSi,
                                    amount = amt,
                                    currency = currencies[selectedCurrencyIndex],
                                    note = noteString,
                                    isIncome = isIncomeSelected
                                )

                                // Success simulation
                                saveSuccess = true
                                isSaving = false
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("button_add_transaction"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "එක් කරන්න (Add)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // 3. Bottom Cards / Smart Tips section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                icon = Icons.Default.Lightbulb,
                title = "Smart Tips",
                desc = "විස්තරාත්මක සටහන් ඔබේ වියදම් හොඳින් පාලනය කිරීමට උදවු වේ.",
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                icon = Icons.Default.Shield,
                title = "Secure Sync",
                desc = "ඔබේ දත්ත ආරක්ෂිතව අපගේ Cloud සමඟ සමමුහුර්ත වේ.",
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                icon = Icons.Default.AutoAwesome,
                title = "Categories",
                desc = "වියදම් වර්ගීකරණයෙන් මාසික අයවැය විශ්ලේෂණය කරන්න.",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun InfoCard(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                lineHeight = 16.sp
            )
        }
    }
}
