package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // format YYYY-MM-DD
    val category: String, // e.g. Groceries, Salary, Transport, Utilities, Shopping, Rent, Food, Other
    val categorySinhala: String, // e.g. බඩුමුට්ටු, වැටුප්, ප්‍රවාහනය, උපයෝගිතා, සාප්පු සවාරි, කුලිය, ආහාර, වෙනත්
    val amount: Double,
    val currency: String, // LKR, USD, EUR
    val note: String,
    val isIncome: Boolean, // true for income, false for expense
    val status: String = "COMPLETED", // COMPLETED, PROCESSING, FAILED
    val timestamp: Long = System.currentTimeMillis()
)

data class TransactionCategory(
    val id: String,
    val nameEn: String,
    val nameSi: String,
    val defaultIsIncome: Boolean,
    val iconName: String
) {
    val displayName: String get() = "$nameEn | $nameSi"
}

val PREDEFINED_CATEGORIES = listOf(
    TransactionCategory("groceries", "Groceries", "බඩුමුට්ටු", false, "shopping_cart"),
    TransactionCategory("salary", "Salary", "වැටුප්", true, "work"),
    TransactionCategory("transport", "Transport", "ප්‍රවාහනය", false, "commute"),
    TransactionCategory("utilities", "Utilities", "උපයෝගිතා", false, "bolt"),
    TransactionCategory("shopping", "Shopping", "සාප්පු සවාරි", false, "shopping_bag"),
    TransactionCategory("food", "Food", "ආහාර", false, "restaurant"),
    TransactionCategory("rent", "Rent", "කුලිය", false, "home"),
    TransactionCategory("other", "Other", "වෙනත්", false, "category")
)
