package com.furkanyildirim.learningcompose.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.data.local.TodoDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("todo_id", 0)
        val todoTitle = intent.getStringExtra("todo_title") ?: context.getString(R.string.notification_title)
        val action = intent.action

        when (action) {
            ACTION_COMPLETE -> handleComplete(context, todoId)
            ACTION_SNOOZE -> handleSnooze(context, todoId)
            else -> {
                Log.d("AlarmReceiver", "Alarm received for todoId=$todoId")
                NotificationHelper.showNotification(context, todoId, todoTitle)
            }
        }
    }

    private fun handleComplete(context: Context, todoId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = TodoDatabase.getDatabase(context).todoDao()
            val todo = dao.todoById(todoId) ?: return@launch
            val updated = todo.copy(
                isCompleted = true,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateTodo(updated)
            if (todo.firebaseId.isNotBlank()) {
                runCatching {
                    FirebaseFirestore.getInstance()
                        .collection("todos")
                        .document(todo.firebaseId)
                        .update(
                            mapOf(
                                "completed" to true,
                                "updatedAt" to updated.updatedAt
                            )
                        )
                }
            }
            AlarmScheduler.cancel(context, todoId)
            cancelNotification(context, todoId)
        }
    }

    private fun handleSnooze(context: Context, todoId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = TodoDatabase.getDatabase(context).todoDao()
            val todo = dao.todoById(todoId) ?: return@launch
            val snoozedTime = System.currentTimeMillis() + SNOOZE_MINUTES * 60_000L
            val updated = todo.copy(
                dueDate = snoozedTime,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateTodo(updated)
            if (todo.firebaseId.isNotBlank()) {
                runCatching {
                    FirebaseFirestore.getInstance()
                        .collection("todos")
                        .document(todo.firebaseId)
                        .update(
                            mapOf(
                                "dueDate" to snoozedTime,
                                "updatedAt" to updated.updatedAt
                            )
                        )
                }
            }
            AlarmScheduler.schedule(context, updated)
            cancelNotification(context, todoId)
        }
    }

    private fun cancelNotification(context: Context, todoId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(todoId)
    }

    companion object {
        const val ACTION_COMPLETE = "com.furkanyildirim.learningcompose.ACTION_COMPLETE"
        const val ACTION_SNOOZE = "com.furkanyildirim.learningcompose.ACTION_SNOOZE"
        private const val SNOOZE_MINUTES = 15
    }
}
