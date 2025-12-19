package com.restaurant.management.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.restaurant.management.data.model.Reservation
import com.restaurant.management.data.model.ReservationStatus
import com.restaurant.management.data.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel(private val reservationRepository: ReservationRepository) : ViewModel() {

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations.asStateFlow()

    private val _reservationState = MutableStateFlow<ReservationState>(ReservationState.Initial)
    val reservationState: StateFlow<ReservationState> = _reservationState.asStateFlow()

    fun loadAllReservations() {
        viewModelScope.launch {
            reservationRepository.getAllReservations().collect { reservationList ->
                _reservations.value = reservationList
            }
        }
    }

    fun loadReservationsByUser(userId: Long) {
        viewModelScope.launch {
            reservationRepository.getReservationsByUser(userId).collect { reservationList ->
                _reservations.value = reservationList
            }
        }
    }

    fun loadReservationsByStatus(status: ReservationStatus) {
        viewModelScope.launch {
            reservationRepository.getReservationsByStatus(status).collect { reservationList ->
                _reservations.value = reservationList
            }
        }
    }

    fun createReservation(reservation: Reservation) {
        viewModelScope.launch {
            _reservationState.value = ReservationState.Loading
            val result = reservationRepository.createReservation(reservation)
            
            result.onSuccess {
                _reservationState.value = ReservationState.Success("Reservation created successfully")
            }.onFailure { error ->
                _reservationState.value = ReservationState.Error(error.message ?: "Failed to create reservation")
            }
        }
    }

    fun updateReservation(reservation: Reservation) {
        viewModelScope.launch {
            _reservationState.value = ReservationState.Loading
            val result = reservationRepository.updateReservation(reservation)
            
            result.onSuccess {
                _reservationState.value = ReservationState.Success("Reservation updated successfully")
            }.onFailure { error ->
                _reservationState.value = ReservationState.Error(error.message ?: "Failed to update reservation")
            }
        }
    }

    fun cancelReservation(reservationId: Long) {
        viewModelScope.launch {
            _reservationState.value = ReservationState.Loading
            val result = reservationRepository.cancelReservation(reservationId)
            
            result.onSuccess {
                _reservationState.value = ReservationState.Success("Reservation cancelled successfully")
            }.onFailure { error ->
                _reservationState.value = ReservationState.Error(error.message ?: "Failed to cancel reservation")
            }
        }
    }

    fun clearState() {
        _reservationState.value = ReservationState.Initial
    }
}

sealed class ReservationState {
    object Initial : ReservationState()
    object Loading : ReservationState()
    data class Success(val message: String) : ReservationState()
    data class Error(val message: String) : ReservationState()
}

class ReservationViewModelFactory(private val reservationRepository: ReservationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationViewModel(reservationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
