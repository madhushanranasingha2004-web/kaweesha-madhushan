package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Transaction
import com.example.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TransactionRepository(database.transactionDao())
        
        // Populate database with mock data if it's empty
        viewModelScope.launch {
            if (repository.getCount() == 0) {
                populateMockData()
            }
        }
    }

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filter and search query states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>("All")
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactions,
        _searchQuery,
        _selectedCategoryFilter
    ) { list, query, catFilter ->
        list.filter { tx ->
            val matchesQuery = query.isEmpty() || 
                tx.category.contains(query, ignoreCase = true) ||
                tx.categorySinhala.contains(query, ignoreCase = true) ||
                tx.note.contains(query, ignoreCase = true)
            
            val matchesCat = catFilter == null || catFilter == "All" || tx.category == catFilter
            
            matchesQuery && matchesCat
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Totals calculated dynamically from database
    val totalIncome: StateFlow<Double> = transactions.map { list ->
        list.filter { it.isIncome }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalExpenses: StateFlow<Double> = transactions.map { list ->
        list.filter { !it.isIncome }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val currentBalance: StateFlow<Double> = transactions.map { list ->
        val incomes = list.filter { it.isIncome }.sumOf { it.amount }
        val expenses = list.filter { !it.isIncome }.sumOf { it.amount }
        incomes - expenses
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun addTransaction(
        date: String,
        category: String,
        categorySinhala: String,
        amount: Double,
        currency: String,
        note: String,
        isIncome: Boolean,
        status: String = "COMPLETED"
    ) {
        viewModelScope.launch {
            repository.insert(
                Transaction(
                    date = date,
                    category = category,
                    categorySinhala = categorySinhala,
                    amount = amount,
                    currency = currency,
                    note = note,
                    isIncome = isIncome,
                    status = status,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    private suspend fun populateMockData() {
        val now = System.currentTimeMillis()
        val mockTransactions = listOf(
            Transaction(
                date = "2023-10-25",
                category = "Other",
                categorySinhala = "වෙනත්",
                amount = 40400.0,
                currency = "LKR",
                note = "Freelance consulting services",
                isIncome = true,
                status = "COMPLETED",
                timestamp = now - 1 * 3600000
            ),
            Transaction(
                date = "2023-10-24",
                category = "Groceries",
                categorySinhala = "බඩුමුට්ටු",
                amount = 12450.0,
                currency = "LKR",
                note = "Weekly grocery shopping at supermarket",
                isIncome = false,
                status = "COMPLETED",
                timestamp = now - 2 * 3600000
            ),
            Transaction(
                date = "2023-10-23",
                category = "Salary",
                categorySinhala = "වැටුප්",
                amount = 85000.0,
                currency = "LKR",
                note = "Monthly salary deposit",
                isIncome = true,
                status = "COMPLETED",
                timestamp = now - 3 * 3600000
            ),
            Transaction(
                date = "2023-10-22",
                category = "Transport",
                categorySinhala = "ප්‍රවාහනය",
                amount = 2500.0,
                currency = "LKR",
                note = "Fuel and highway tolls",
                isIncome = false,
                status = "PROCESSING",
                timestamp = now - 4 * 3600000
            ),
            Transaction(
                date = "2023-10-20",
                category = "Utilities",
                categorySinhala = "උපයෝගිතා",
                amount = 8900.0,
                currency = "LKR",
                note = "Electricity and internet bills",
                isIncome = false,
                status = "COMPLETED",
                timestamp = now - 5 * 3600000
            ),
            Transaction(
                date = "2023-10-19",
                category = "Shopping",
                categorySinhala = "සාප්පු සවාරි",
                amount = 5200.0,
                currency = "LKR",
                note = "Clothing and active wear purchase",
                isIncome = false,
                status = "FAILED",
                timestamp = now - 6 * 3600000
            ),
            Transaction(
                date = "2023-10-15",
                category = "Rent",
                categorySinhala = "කුලිය",
                amount = 39180.0,
                currency = "LKR",
                note = "Apartment monthly rent",
                isIncome = false,
                status = "COMPLETED",
                timestamp = now - 7 * 3600000
            )
        )

        for (tx in mockTransactions) {
            repository.insert(tx)
        }
    }
}
