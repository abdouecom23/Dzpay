package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PaymentRepository
import com.example.data.Transaction
import com.example.data.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: PaymentRepository) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun simulateTransaction() {
        viewModelScope.launch {
            val isCredit = (0..1).random() == 0
            val amount = (1000..500000).random().toLong()
            val desc = if (isCredit) "Virement de Salaire" else "Supermarché Uno"
            val provider = if (isCredit) "BNA" else "Edahabia"
            
            val newTransaction = Transaction(
                amount = amount,
                type = if (isCredit) "CREDIT" else "DEBIT",
                status = if ((0..5).random() > 0) "SUCCESS" else "PENDING",
                reference = "TXN-${System.currentTimeMillis()}",
                provider = provider,
                description = desc
            )
            repository.addTransaction(newTransaction)
            
            userProfile.value?.let { profile ->
                val newBalance = if (isCredit) profile.balance + amount else profile.balance - amount
                repository.updateBalance(profile.id, newBalance)
            }
        }
    }

    fun initMockUser() {
        viewModelScope.launch {
            if (userProfile.value == null) {
                repository.saveProfile(
                    UserProfile(
                        id = "user_123",
                        email = "reda.mansouri@dzpay.com",
                        phone = "+213 555 12 34 56",
                        role = "MERCHANT",
                        kycStatus = "PENDING",
                        balance = 14250000 // 142,500.00 DZD
                    )
                )
                
                // Add initial transactions from the design
                repository.addTransaction(Transaction(
                    amount = 425000,
                    type = "DEBIT",
                    status = "SUCCESS",
                    reference = "REF001",
                    provider = "Edahabia",
                    description = "Supermarché Uno"
                ))
                repository.addTransaction(Transaction(
                    amount = 8500000,
                    type = "CREDIT",
                    status = "SUCCESS",
                    reference = "REF002",
                    provider = "BNA",
                    description = "Virement de Salaire"
                ))
            }
        }
    }
}
