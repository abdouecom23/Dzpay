package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Transaction
import com.example.data.UserProfile
import com.example.ui.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel, onNavigateToKYC: () -> Unit) {
    val transactions by viewModel.transactions.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initMockUser()
    }

    Scaffold(
        topBar = {
            HeaderSection(userProfile)
        },
        bottomBar = {
            DZBottomBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.simulateTransaction() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Simulate Payment")
            }
        },
        containerColor = Color(0xFFF7F9FC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7F9FC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Card
            item {
                VibrantBalanceCard(userProfile?.balance ?: 0L)
            }

            // Quick Actions
            item {
                QuickActionsGrid()
            }

            // KYC Banner
            if (userProfile?.kycStatus != "VERIFIED") {
                item {
                    KYCBanner(onNavigateToKYC)
                }
            }

            // Activities Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Activités Récentes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    TextButton(onClick = { /* See All */ }) {
                        Text("Tout voir", color = Color(0xFF0061A4), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Activities List
            if (transactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No activities yet.", color = Color.Gray)
                    }
                }
            } else {
                items(transactions) { transaction ->
                    VibrantTransactionItem(transaction)
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun HeaderSection(userProfile: UserProfile?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFFD3E3FD)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "RM",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001C38)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Bonjour,",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF44474E),
                    fontSize = 11.sp
                )
                Text(
                    userProfile?.email?.split("@")?.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Reda Mansouri",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(24.dp))
            }
            Surface(
                modifier = Modifier.height(36.dp),
                shape = CircleShape,
                color = Color(0xFFE1E2E8)
            ) {
                Box(modifier = Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                    Text("عربي", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VibrantBalanceCard(balance: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0061A4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative glow
            Surface(
                modifier = Modifier.size(120.dp).offset(x = 240.dp, y = (-40).dp),
                color = Color.White.copy(alpha = 0.1f),
                shape = CircleShape
            ) {}

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Solde Total (DZD)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            formatAmount(balance),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "DA",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "ID MARCHAND",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            "DZ-992-104",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFF34D399), CircleShape))
                            Text(
                                "SATIM CONNECTÉ",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionItem("📲", "Payer", Color(0xFFD3E3FD))
        QuickActionItem("➕", "Recharger", Color(0xFFE8DEF8))
        QuickActionItem("🔄", "Virement", Color(0xFFFFE08D))
        QuickActionItem("📁", "Extraits", Color(0xFFD0F8E2))
    }
}

@Composable
fun QuickActionItem(emoji: String, label: String, bgColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = bgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = Color(0xFF44474E))
    }
}

@Composable
fun KYCBanner(onNavigateToKYC: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFEEFC3),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFAD242))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFFFAD242)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("⚠️", fontSize = 18.sp)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Action requise : KYC", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF442E00))
                Text("Vérifiez votre identité pour débloquer le plafond de 200.000 DA.", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = Color(0xFF665500))
            }
            Button(
                onClick = onNavigateToKYC,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Scanner", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF442E00))
            }
        }
    }
}

@Composable
fun VibrantTransactionItem(transaction: Transaction) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1E2E8))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = when {
                    transaction.description?.contains("Uno") == true -> Color(0xFFFFEDD5)
                    transaction.description?.contains("Salaire") == true -> Color(0xFFDBEAFE)
                    else -> Color(0xFFF3E8FF)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        when {
                            transaction.description?.contains("Uno") == true -> "🛒"
                            transaction.description?.contains("Salaire") == true -> "💼"
                            else -> "🌐"
                        },
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.description ?: "Transaction", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                Text("Aujourd'hui, 09:15 • ${transaction.provider}", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = Color(0xFF74777F))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    (if (transaction.type == "CREDIT") "+" else "-") + formatAmount(transaction.amount) + " DA",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type == "CREDIT") Color(0xFF059669) else Color(0xFFDC2626)
                )
                Surface(
                    color = if (transaction.status == "SUCCESS") Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        if (transaction.status == "SUCCESS") "Réussi" else "En attente",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (transaction.status == "SUCCESS") Color(0xFF059669) else Color(0xFFD97706)
                    )
                }
            }
        }
    }
}

@Composable
fun DZBottomBar() {
    NavigationBar(
        modifier = Modifier.height(80.dp),
        containerColor = Color(0xFFEFF1F8),
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Text("🏠", fontSize = 20.sp) },
            label = { Text("Accueil", fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFFD3E3FD),
                selectedTextColor = Color(0xFF001C38)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Text("💳", fontSize = 20.sp) },
            label = { Text("Mes Cartes") },
            modifier = Modifier.graphicsLayer { alpha = 0.6f }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Text("📊", fontSize = 20.sp) },
            label = { Text("Analyses") },
            modifier = Modifier.graphicsLayer { alpha = 0.6f }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Text("⚙️", fontSize = 20.sp) },
            label = { Text("Réglages") },
            modifier = Modifier.graphicsLayer { alpha = 0.6f }
        )
    }
}

fun formatAmount(amountCentimes: Long): String {
    val amount = amountCentimes / 100.0
    val format = NumberFormat.getNumberInstance(Locale.FRANCE)
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return format.format(amount)
}
