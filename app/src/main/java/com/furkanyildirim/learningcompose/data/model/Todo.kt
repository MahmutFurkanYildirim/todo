package com.furkanyildirim.learningcompose.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val priority: String = Priority.MEDIUM.name,
    val category: String = Category.OTHER.name,
    val project: String = "",
    val tags: String = "",
    val repeatIntervalDays: Int = 0,
    val repeatRule: String = RepeatRule.NONE,
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val firebaseId: String = ""
)
