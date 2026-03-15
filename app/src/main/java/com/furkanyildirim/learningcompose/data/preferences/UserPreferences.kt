package com.furkanyildirim.learningcompose.data.preferences

import android.content.Context
import com.furkanyildirim.learningcompose.data.model.FocusSessionDayStat
import com.furkanyildirim.learningcompose.data.model.SyncTelemetryState
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingCompleted(value: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()
    }

    fun isAuthChoiceCompleted(): Boolean = prefs.getBoolean(KEY_AUTH_CHOICE_DONE, false)

    fun setAuthChoiceCompleted(value: Boolean) {
        prefs.edit().putBoolean(KEY_AUTH_CHOICE_DONE, value).apply()
    }

    fun isDarkTheme(): Boolean = prefs.getBoolean(KEY_DARK_THEME, true)

    fun setDarkTheme(value: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()
    }

    fun getLanguageCode(): String = prefs.getString(KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE_CODE).orEmpty()

    fun setLanguageCode(value: String) {
        prefs.edit().putString(KEY_LANGUAGE_CODE, value).apply()
    }

    fun getRecentSearches(): List<String> {
        return prefs.getString(KEY_RECENT_SEARCHES, "")
            .orEmpty()
            .split(RECENT_SEARCH_DELIMITER)
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    fun saveRecentSearch(query: String, maxItems: Int = 5) {
        val normalized = query.trim()
        if (normalized.isBlank()) return

        val updated = buildList {
            add(normalized)
            addAll(getRecentSearches().filterNot { it.equals(normalized, ignoreCase = true) })
        }.take(maxItems)

        prefs.edit()
            .putString(KEY_RECENT_SEARCHES, updated.joinToString(RECENT_SEARCH_DELIMITER))
            .apply()
    }

    fun clearRecentSearches() {
        prefs.edit().remove(KEY_RECENT_SEARCHES).apply()
    }

    fun getFocusWorkMinutes(): Int = prefs.getInt(KEY_FOCUS_WORK_MINUTES, DEFAULT_FOCUS_WORK_MINUTES)

    fun setFocusWorkMinutes(value: Int) {
        prefs.edit().putInt(KEY_FOCUS_WORK_MINUTES, value.coerceIn(10, 60)).apply()
    }

    fun getFocusBreakMinutes(): Int = prefs.getInt(KEY_FOCUS_BREAK_MINUTES, DEFAULT_FOCUS_BREAK_MINUTES)

    fun setFocusBreakMinutes(value: Int) {
        prefs.edit().putInt(KEY_FOCUS_BREAK_MINUTES, value.coerceIn(3, 30)).apply()
    }

    fun isFocusAutoStartBreakEnabled(): Boolean = prefs.getBoolean(KEY_FOCUS_AUTO_START_BREAK, true)

    fun setFocusAutoStartBreakEnabled(value: Boolean) {
        prefs.edit().putBoolean(KEY_FOCUS_AUTO_START_BREAK, value).apply()
    }

    fun isFocusAutoStartWorkEnabled(): Boolean = prefs.getBoolean(KEY_FOCUS_AUTO_START_WORK, false)

    fun setFocusAutoStartWorkEnabled(value: Boolean) {
        prefs.edit().putBoolean(KEY_FOCUS_AUTO_START_WORK, value).apply()
    }

    fun getFocusSessionHistory(): List<FocusSessionDayStat> {
        return prefs.getString(KEY_FOCUS_HISTORY, "")
            .orEmpty()
            .split(RECENT_SEARCH_DELIMITER)
            .mapNotNull { token ->
                val parts = token.split(FOCUS_HISTORY_FIELD_DELIMITER)
                if (parts.size != 3) return@mapNotNull null
                val dayKey = parts[0]
                val sessions = parts[1].toIntOrNull() ?: return@mapNotNull null
                val minutes = parts[2].toIntOrNull() ?: return@mapNotNull null
                FocusSessionDayStat(dayKey = dayKey, sessions = sessions, minutes = minutes)
            }
    }

    fun recordFocusSession(
        focusedMinutes: Int,
        completedAt: Long = System.currentTimeMillis()
    ) {
        if (focusedMinutes <= 0) return

        val dayKey = dayKey(completedAt)
        val updatedMap = getFocusSessionHistory()
            .associateBy { it.dayKey }
            .toMutableMap()

        val current = updatedMap[dayKey]
        val next = FocusSessionDayStat(
            dayKey = dayKey,
            sessions = (current?.sessions ?: 0) + 1,
            minutes = (current?.minutes ?: 0) + focusedMinutes
        )
        updatedMap[dayKey] = next

        val serialized = updatedMap.values
            .sortedByDescending { it.dayKey }
            .take(MAX_FOCUS_HISTORY_DAYS)
            .joinToString(RECENT_SEARCH_DELIMITER) { stat ->
                listOf(stat.dayKey, stat.sessions, stat.minutes).joinToString(FOCUS_HISTORY_FIELD_DELIMITER)
            }

        prefs.edit().putString(KEY_FOCUS_HISTORY, serialized).apply()
    }

    fun getSyncTelemetryState(): SyncTelemetryState {
        return SyncTelemetryState(
            lastSuccessAt = prefs.getLong(KEY_SYNC_LAST_SUCCESS_AT, 0L),
            totalSuccessCount = prefs.getInt(KEY_SYNC_TOTAL_SUCCESS, 0),
            totalFailureCount = prefs.getInt(KEY_SYNC_TOTAL_FAILURE, 0),
            consecutiveFailureCount = prefs.getInt(KEY_SYNC_CONSECUTIVE_FAILURE, 0),
            totalRetryCount = prefs.getInt(KEY_SYNC_TOTAL_RETRY, 0),
            lastError = prefs.getString(KEY_SYNC_LAST_ERROR, "").orEmpty()
        )
    }

    fun recordSyncSuccess(retriesUsed: Int) {
        val nextSuccess = prefs.getInt(KEY_SYNC_TOTAL_SUCCESS, 0) + 1
        val nextRetries = prefs.getInt(KEY_SYNC_TOTAL_RETRY, 0) + retriesUsed.coerceAtLeast(0)
        prefs.edit()
            .putLong(KEY_SYNC_LAST_SUCCESS_AT, System.currentTimeMillis())
            .putInt(KEY_SYNC_TOTAL_SUCCESS, nextSuccess)
            .putInt(KEY_SYNC_CONSECUTIVE_FAILURE, 0)
            .putInt(KEY_SYNC_TOTAL_RETRY, nextRetries)
            .putString(KEY_SYNC_LAST_ERROR, "")
            .apply()
    }

    fun recordSyncFailure(error: String, retriesUsed: Int) {
        val nextFailure = prefs.getInt(KEY_SYNC_TOTAL_FAILURE, 0) + 1
        val nextConsecutive = prefs.getInt(KEY_SYNC_CONSECUTIVE_FAILURE, 0) + 1
        val nextRetries = prefs.getInt(KEY_SYNC_TOTAL_RETRY, 0) + retriesUsed.coerceAtLeast(0)
        prefs.edit()
            .putInt(KEY_SYNC_TOTAL_FAILURE, nextFailure)
            .putInt(KEY_SYNC_CONSECUTIVE_FAILURE, nextConsecutive)
            .putInt(KEY_SYNC_TOTAL_RETRY, nextRetries)
            .putString(KEY_SYNC_LAST_ERROR, error.take(120))
            .apply()
    }

    fun getPendingRemoteDeleteIds(): Set<String> {
        return prefs.getString(KEY_PENDING_REMOTE_DELETES, "")
            .orEmpty()
            .split(RECENT_SEARCH_DELIMITER)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()
    }

    fun addPendingRemoteDeleteId(firebaseId: String) {
        if (firebaseId.isBlank()) return
        val updated = getPendingRemoteDeleteIds().toMutableSet().apply { add(firebaseId) }
        prefs.edit()
            .putString(KEY_PENDING_REMOTE_DELETES, updated.joinToString(RECENT_SEARCH_DELIMITER))
            .apply()
    }

    fun removePendingRemoteDeleteId(firebaseId: String) {
        if (firebaseId.isBlank()) return
        val updated = getPendingRemoteDeleteIds().toMutableSet().apply { remove(firebaseId) }
        prefs.edit()
            .putString(KEY_PENDING_REMOTE_DELETES, updated.joinToString(RECENT_SEARCH_DELIMITER))
            .apply()
    }

    private fun dayKey(timestamp: Long): String {
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.US)
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return formatter.format(calendar.time)
    }

    companion object {
        private const val PREF_NAME = "learning_compose_prefs"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_AUTH_CHOICE_DONE = "auth_choice_done"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_LANGUAGE_CODE = "language_code"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
        private const val KEY_FOCUS_WORK_MINUTES = "focus_work_minutes"
        private const val KEY_FOCUS_BREAK_MINUTES = "focus_break_minutes"
        private const val KEY_FOCUS_AUTO_START_BREAK = "focus_auto_start_break"
        private const val KEY_FOCUS_AUTO_START_WORK = "focus_auto_start_work"
        private const val KEY_FOCUS_HISTORY = "focus_history"
        private const val KEY_SYNC_LAST_SUCCESS_AT = "sync_last_success_at"
        private const val KEY_SYNC_TOTAL_SUCCESS = "sync_total_success"
        private const val KEY_SYNC_TOTAL_FAILURE = "sync_total_failure"
        private const val KEY_SYNC_CONSECUTIVE_FAILURE = "sync_consecutive_failure"
        private const val KEY_SYNC_TOTAL_RETRY = "sync_total_retry"
        private const val KEY_SYNC_LAST_ERROR = "sync_last_error"
        private const val KEY_PENDING_REMOTE_DELETES = "pending_remote_deletes"
        private const val RECENT_SEARCH_DELIMITER = "||"
        private const val FOCUS_HISTORY_FIELD_DELIMITER = ":"
        private const val DEFAULT_LANGUAGE_CODE = "tr"
        private const val DEFAULT_FOCUS_WORK_MINUTES = 25
        private const val DEFAULT_FOCUS_BREAK_MINUTES = 5
        private const val MAX_FOCUS_HISTORY_DAYS = 30
    }
}
