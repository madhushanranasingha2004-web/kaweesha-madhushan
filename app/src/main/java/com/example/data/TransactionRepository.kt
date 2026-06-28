package com.example.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun getCount(): Int = transactionDao.getTransactionCount()

    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun delete(id: Long) {
        transactionDao.deleteTransaction(id)
    }

    suspend fun clearAll() {
        transactionDao.clearAll()
    }
}
