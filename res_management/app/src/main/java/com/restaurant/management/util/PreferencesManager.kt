package com.restaurant.management.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        val USER_ID = longPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_ROLE = stringPreferencesKey("user_role")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFY_NEW_RESERVATIONS = booleanPreferencesKey("notify_new_reservations")
        val NOTIFY_RESERVATION_CHANGES = booleanPreferencesKey("notify_reservation_changes")
    }

    suspend fun saveUserSession(userId: Long, email: String, name: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
            preferences[USER_ROLE] = role
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    val userId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    val userRole: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ROLE]
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun updateNotificationSettings(
        enabled: Boolean? = null,
        notifyNewReservations: Boolean? = null,
        notifyReservationChanges: Boolean? = null
    ) {
        context.dataStore.edit { preferences ->
            enabled?.let { preferences[NOTIFICATIONS_ENABLED] = it }
            notifyNewReservations?.let { preferences[NOTIFY_NEW_RESERVATIONS] = it }
            notifyReservationChanges?.let { preferences[NOTIFY_RESERVATION_CHANGES] = it }
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    val notifyNewReservations: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFY_NEW_RESERVATIONS] ?: true
    }

    val notifyReservationChanges: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFY_RESERVATION_CHANGES] ?: true
    }
}
