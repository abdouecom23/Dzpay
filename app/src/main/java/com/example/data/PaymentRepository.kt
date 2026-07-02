package com.example.data

import kotlinx.coroutines.flow.Flow

class PaymentRepository(
    private val transactionDao: TransactionDao,
    private val userProfileDao: UserProfileDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.saveUserProfile(profile)
    }

    suspend fun updateBalance(userId: String, amount: Long) {
        userProfileDao.updateBalance(userId, amount)
    }
}
