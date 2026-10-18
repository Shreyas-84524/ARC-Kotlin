package com.arc.arc.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.arcDataStore: DataStore<Preferences> by preferencesDataStore(name = "arc_settings")

data class AppPreferences(
    val themeMode: String = "dark", // "dark", "light", "system"
    val onboardingCompleted: Boolean = true,
    val remTime: String = "09:00",
    val masterNotificationsEnabled: Boolean = true,
    val activeTaskSort: String = "DueDate",
    val activeTaskCategoryFilter: String? = null,
    val activeTaskStatusFilter: String = "All",
    val activeTaskDateFilter: String = "All",
)

/**
 * DataStore foundation for non-sensitive user preferences and application settings.
 * Sensitive data (credentials, cryptographic keys) must NEVER be stored here.
 */
class AppPreferencesDataStore(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_REM_TIME = stringPreferencesKey("rem_time")
        val KEY_MASTER_NOTIFICATIONS = booleanPreferencesKey("master_notifications_enabled")
        val KEY_TASK_SORT = stringPreferencesKey("task_sort")
        val KEY_TASK_CATEGORY_FILTER = stringPreferencesKey("task_category_filter")
        val KEY_TASK_STATUS_FILTER = stringPreferencesKey("task_status_filter")
        val KEY_TASK_DATE_FILTER = stringPreferencesKey("task_date_filter")
    }

    val preferencesFlow: Flow<AppPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            AppPreferences(
                themeMode = prefs[KEY_THEME_MODE] ?: "dark",
                onboardingCompleted = prefs[KEY_ONBOARDING_COMPLETED] ?: true,
                remTime = prefs[KEY_REM_TIME] ?: "09:00",
                masterNotificationsEnabled = prefs[KEY_MASTER_NOTIFICATIONS] ?: true,
                activeTaskSort = prefs[KEY_TASK_SORT] ?: "DueDate",
                activeTaskCategoryFilter = prefs[KEY_TASK_CATEGORY_FILTER]?.takeIf { it != "ALL" },
                activeTaskStatusFilter = prefs[KEY_TASK_STATUS_FILTER] ?: "All",
                activeTaskDateFilter = prefs[KEY_TASK_DATE_FILTER] ?: "All",
            )
        }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setRemTime(time: String) {
        dataStore.edit { prefs ->
            prefs[KEY_REM_TIME] = time
        }
    }

    suspend fun setMasterNotifications(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_MASTER_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setTaskSort(sort: String) {
        dataStore.edit { prefs ->
            prefs[KEY_TASK_SORT] = sort
        }
    }

    suspend fun setTaskCategoryFilter(category: String?) {
        dataStore.edit { prefs ->
            if (category == null) {
                prefs.remove(KEY_TASK_CATEGORY_FILTER)
            } else {
                prefs[KEY_TASK_CATEGORY_FILTER] = category
            }
        }
    }

    suspend fun setTaskStatusFilter(status: String) {
        dataStore.edit { prefs ->
            prefs[KEY_TASK_STATUS_FILTER] = status
        }
    }

    suspend fun setTaskDateFilter(dateFilter: String) {
        dataStore.edit { prefs ->
            prefs[KEY_TASK_DATE_FILTER] = dateFilter
        }
    }
}
