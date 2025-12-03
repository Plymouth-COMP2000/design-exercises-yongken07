package com.restaurant.management.ui.guest

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.restaurant.management.RestaurantApplication
import com.restaurant.management.data.model.User
import com.restaurant.management.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun GuestSettingsScreen(
    authViewModel: AuthViewModel,
    currentUser: User?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as RestaurantApplication
    val scope = rememberCoroutineScope()
    
    var notificationsEnabled by remember { mutableStateOf(currentUser?.notificationsEnabled ?: true) }
    var notifyReservationChanges by remember { mutableStateOf(currentUser?.notifyOnReservationChanges ?: true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Account Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Column {
                        Text(
                            text = "Name",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = currentUser?.name ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Column {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = currentUser?.email ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Notification Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Notifications",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Receive all app notifications",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            notificationsEnabled = it
                            scope.launch {
                                currentUser?.let { user ->
                                    application.userRepository.updateNotificationSettings(
                                        userId = user.id,
                                        notificationsEnabled = it
                                    )
                                    application.preferencesManager.updateNotificationSettings(enabled = it)
                                }
                            }
                        }
                    )
                }
                
                Divider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reservation Updates",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Get notified when your reservation is modified or cancelled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = notifyReservationChanges,
                        enabled = notificationsEnabled,
                        onCheckedChange = {
                            notifyReservationChanges = it
                            scope.launch {
                                currentUser?.let { user ->
                                    application.userRepository.updateNotificationSettings(
                                        userId = user.id,
                                        notifyOnReservationChanges = it
                                    )
                                    application.preferencesManager.updateNotificationSettings(notifyReservationChanges = it)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
