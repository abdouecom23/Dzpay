package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class, UserProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun userProfileDao(): UserProfileDao
}
