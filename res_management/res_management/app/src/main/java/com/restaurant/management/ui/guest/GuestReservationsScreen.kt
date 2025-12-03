package com.restaurant.management.ui.guest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.restaurant.management.data.model.Reservation
import com.restaurant.management.data.model.ReservationStatus
import com.restaurant.management.viewmodel.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun GuestReservationsScreen(
    reservationViewModel: ReservationViewModel,
    currentUserId: Long?,
    currentUserName: String,
    currentUserEmail: String,
    modifier: Modifier = Modifier
) {
    val reservations by reservationViewModel.reservations.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }

    LaunchedEffect(currentUserId) {
        currentUserId?.let {
            reservationViewModel.loadReservationsByUser(it)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Reservations",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            FloatingActionButton(
                onClick = {
                    selectedReservation = null
                    showAddDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Reservation")
            }
        }

        if (reservations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No reservations yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap + to make your first reservation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reservations) { reservation ->
                    GuestReservationCard(
                        reservation = reservation,
                        onEdit = {
                            selectedReservation = reservation
                            showAddDialog = true
                        },
                        onCancel = {
                            reservationViewModel.cancelReservation(reservation.id)
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog && currentUserId != null) {
        AddEditReservationDialog(
            reservation = selectedReservation,
            userId = currentUserId,
            userName = currentUserName,
            userEmail = currentUserEmail,
            onDismiss = { showAddDialog = false },
            onSave = { reservation ->
                if (selectedReservation == null) {
                    reservationViewModel.createReservation(reservation)
                } else {
                    reservationViewModel.updateReservation(reservation)
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun GuestReservationCard(
    reservation: Reservation,
    onEdit: () -> Unit,
    onCancel: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (reservation.status) {
                ReservationStatus.CONFIRMED -> MaterialTheme.colorScheme.surface
                ReservationStatus.MODIFIED -> MaterialTheme.colorScheme.secondaryContainer
                ReservationStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = reservation.reservationDate,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = reservation.reservationTime,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TableRestaurant,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Table ${reservation.tableNumber} • ${reservation.numberOfGuests} guests",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    if (reservation.specialRequests.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Note,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = reservation.specialRequests,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                
                if (reservation.status != ReservationStatus.CANCELLED) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onEdit) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showCancelDialog = true }) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AssistChip(
                onClick = { },
                label = { Text(reservation.status.name) },
                leadingIcon = {
                    Icon(
                        when (reservation.status) {
                            ReservationStatus.CONFIRMED -> Icons.Default.CheckCircle
                            ReservationStatus.MODIFIED -> Icons.Default.Edit
                            ReservationStatus.CANCELLED -> Icons.Default.Cancel
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Reservation") },
            text = { Text("Are you sure you want to cancel this reservation?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancel()
                        showCancelDialog = false
                    }
                ) {
                    Text("Cancel Reservation", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Reservation")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReservationDialog(
    reservation: Reservation?,
    userId: Long,
    userName: String,
    userEmail: String,
    onDismiss: () -> Unit,
    onSave: (Reservation) -> Unit
) {
    var date by remember { mutableStateOf(reservation?.reservationDate ?: LocalDate.now().toString()) }
    var time by remember { mutableStateOf(reservation?.reservationTime ?: "18:00") }
    var tableNumber by remember { mutableStateOf(reservation?.tableNumber?.toString() ?: "1") }
    var numberOfGuests by remember { mutableStateOf(reservation?.numberOfGuests?.toString() ?: "2") }
    var specialRequests by remember { mutableStateOf(reservation?.specialRequests ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (reservation == null) "New Reservation" else "Edit Reservation",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (HH:MM)") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = tableNumber,
                    onValueChange = { tableNumber = it },
                    label = { Text("Table Number") },
                    leadingIcon = { Icon(Icons.Default.TableRestaurant, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = numberOfGuests,
                    onValueChange = { numberOfGuests = it },
                    label = { Text("Number of Guests") },
                    leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = specialRequests,
                    onValueChange = { specialRequests = it },
                    label = { Text("Special Requests (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Note, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val tableNum = tableNumber.toIntOrNull() ?: 1
                            val guests = numberOfGuests.toIntOrNull() ?: 2
                            val newReservation = Reservation(
                                id = reservation?.id ?: 0,
                                userId = userId,
                                userName = userName,
                                userEmail = userEmail,
                                tableNumber = tableNum,
                                numberOfGuests = guests,
                                reservationDate = date,
                                reservationTime = time,
                                specialRequests = specialRequests,
                                status = reservation?.status ?: ReservationStatus.CONFIRMED
                            )
                            onSave(newReservation)
                        },
                        enabled = date.isNotBlank() && time.isNotBlank() && 
                                  tableNumber.toIntOrNull() != null && numberOfGuests.toIntOrNull() != null
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
