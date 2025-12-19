package com.restaurant.management.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.restaurant.management.RestaurantApplication
import com.restaurant.management.data.model.UserRole
import com.restaurant.management.ui.auth.LoginScreen
import com.restaurant.management.ui.auth.RegisterScreen
import com.restaurant.management.ui.guest.GuestHomeScreen
import com.restaurant.management.ui.staff.StaffHomeScreen
import com.restaurant.management.viewmodel.AuthViewModel
import com.restaurant.management.viewmodel.AuthViewModelFactory
import androidx.compose.ui.platform.LocalContext

@Composable
fun RestaurantApp() {
    val context = LocalContext.current
    val application = context.applicationContext as RestaurantApplication
    
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            application.userRepository,
            application.preferencesManager
        )
    )
    
    val currentUser by authViewModel.currentUser.collectAsState()
    val navController = rememberNavController()
    
    val startDestination = if (currentUser != null) {
        when (currentUser?.role) {
            UserRole.STAFF -> "staff_home"
            UserRole.GUEST -> "guest_home"
            else -> "login"
        }
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        composable("register") {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        composable("staff_home") {
            StaffHomeScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        composable("guest_home") {
            GuestHomeScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
