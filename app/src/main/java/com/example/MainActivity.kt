package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.PaymentRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.KYCScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.DZPayTheme
import com.example.ui.viewmodel.DashboardViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "dzpay-db"
        ).build()
        
        val repository = PaymentRepository(db.transactionDao(), db.userProfileDao())
        
        enableEdgeToEdge()
        setContent {
            DZPayTheme {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(onAuthenticated = {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        })
                    }
                    composable("dashboard") {
                        val viewModel: DashboardViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return DashboardViewModel(repository) as T
                                }
                            }
                        )
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToKYC = { navController.navigate("kyc") }
                        )
                    }
                    composable("kyc") {
                        KYCScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
