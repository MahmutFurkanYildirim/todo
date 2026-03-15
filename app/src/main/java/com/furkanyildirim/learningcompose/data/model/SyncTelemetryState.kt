package com.furkanyildirim.learningcompose.data.model

data class SyncTelemetryState(
    val lastSuccessAt: Long = 0L,
    val totalSuccessCount: Int = 0,
    val totalFailureCount: Int = 0,
    val consecutiveFailureCount: Int = 0,
    val totalRetryCount: Int = 0,
    val lastError: String = ""
)
