package com.restaurant.management.ui.guest

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.restaurant.management.RestaurantApplication
import com.restaurant.management.viewmodel.AuthViewModel
import com.restaurant.management.viewmodel.MenuViewModel
import com.restaurant.management.viewmodel.MenuViewModelFactory
import com.restaurant.management.viewmodel.ReservationViewModel
import com.restaurant.management.viewmodel.ReservationViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val application = context.applicationContext as RestaurantApplication
    
    val menuViewModel: MenuViewModel = viewModel(
        factory = MenuViewModelFactory(application.menuRepository)
    )
    
    val reservationViewModel: ReservationViewModel = viewModel(
        factory = ReservationViewModelFactory(application.reservationRepository)
    )
    
    var selectedTab by remember { mutableStateOf(0) }
    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = "Menu") },
                    label = { Text("Menu") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Book, contentDescription = "My Reservations") },
                    label = { Text("Reservations") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> GuestMenuScreen(
                menuViewModel = menuViewModel,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> GuestReservationsScreen(
                reservationViewModel = reservationViewModel,
                currentUserId = currentUser?.id,
                currentUserName = currentUser?.name ?: "",
                currentUserEmail = currentUser?.email ?: "",
                modifier = Modifier.padding(paddingValues)
            )
            2 -> GuestSettingsScreen(
                authViewModel = authViewModel,
                currentUser = currentUser,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
