package com.furkanyildirim.learningcompose.domain.usecase

data class SyncRunReport(
    val totalOperations: Int,
    val failedOperations: Int,
    val retriesUsed: Int,
    val lastErrorMessage: String = ""
)
