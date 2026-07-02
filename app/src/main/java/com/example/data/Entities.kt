package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Long, // In centimes
    val type: String, // "DEBIT" or "CREDIT"
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val reference: String,
    val provider: String, // "SATIM", "GIE_MONETIQUE", "INTERNAL"
    val timestamp: Long = System.currentTimeMillis(),
    val description: String? = null
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String,
    val email: String,
    val phone: String,
    val role: String, // "MERCHANT", "INDIVIDUAL"
    val kycStatus: String, // "VERIFIED", "PENDING", "UNVERIFIED"
    val balance: Long = 0
)
