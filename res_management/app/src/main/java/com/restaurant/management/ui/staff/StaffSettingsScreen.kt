package com.restaurant.management.ui.staff

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
fun StaffSettingsScreen(
    authViewModel: AuthViewModel,
    currentUser: User?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as RestaurantApplication
    val scope = rememberCoroutineScope()
    var remoteUsers by remember { mutableStateOf<List<User>>(emptyList()) }
    var syncInProgress by remember { mutableStateOf(false) }
    var syncError by remember { mutableStateOf<String?>(null) }
    var deleteInProgress by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        syncInProgress = true
        syncError = null
        remoteUsers = application.userRepository.syncRemoteUsersToLocal().getOrElse {
            syncError = it.message
            emptyList()
        }
        syncInProgress = false
    }
    
    var notificationsEnabled by remember { mutableStateOf(currentUser?.notificationsEnabled ?: true) }
    var notifyNewReservations by remember { mutableStateOf(currentUser?.notifyOnNewReservations ?: true) }
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
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                    Column {
                        Text(
                            text = "Role",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = currentUser?.role?.name ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Coursework API Users",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Pulled from /create_user & /read_all_users",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        syncInProgress = true
                                        syncError = null
                                        remoteUsers = application.userRepository.syncRemoteUsersToLocal().getOrElse {
                                            syncError = it.message
                                            emptyList()
                                        }
                                        syncInProgress = false
                                    }
                                },
                                enabled = !syncInProgress
                            ) {
                                if (syncInProgress) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh users")
                                }
                            }
                        }

                        if (syncError != null) {
                            Text(
                                text = syncError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (remoteUsers.isEmpty() && !syncInProgress) {
                            Text(
                                text = "No remote users yet.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        remoteUsers.forEach { user ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(user.name, style = MaterialTheme.typography.bodyLarge)
                                            Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                            Text("Role: ${user.role.name}", style = MaterialTheme.typography.bodySmall)
                                            user.remoteId?.let { rid ->
                                                Text("Remote ID: $rid", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                            }
                                        }
                                        val rid = user.remoteId
                                        IconButton(
                                            onClick = {
                                                if (rid != null) {
                                                    scope.launch {
                                                        deleteInProgress = rid
                                                        val result = application.userRepository.deleteRemoteAndLocal(rid)
                                                        result.onSuccess {
                                                            remoteUsers = remoteUsers.filterNot { it.remoteId == rid }
                                                        }.onFailure { syncError = it.message }
                                                        deleteInProgress = null
                                                    }
                                                }
                                            },
                                            enabled = rid != null && deleteInProgress == null
                                        ) {
                                            if (deleteInProgress == rid) {
                                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                            } else {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete user")
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
                            text = "New Reservations",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Get notified when customers make new reservations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = notifyNewReservations,
                        enabled = notificationsEnabled,
                        onCheckedChange = {
                            notifyNewReservations = it
                            scope.launch {
                                currentUser?.let { user ->
                                    application.userRepository.updateNotificationSettings(
                                        userId = user.id,
                                        notifyOnNewReservations = it
                                    )
                                    application.preferencesManager.updateNotificationSettings(notifyNewReservations = it)
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
                            text = "Reservation Changes",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Get notified when reservations are modified or cancelled",
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
