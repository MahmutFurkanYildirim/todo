package com.furkanyildirim.learningcompose.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.furkanyildirim.learningcompose.data.model.Todo

object AlarmScheduler {

    fun schedule(context: Context, todo: Todo) {
        val dueDate = todo.dueDate ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12+ için izin kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // İzin yoksa inexact alarm kullan
                scheduleInexact(context, alarmManager, todo, dueDate)
                return
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("todo_id", todo.id)
            putExtra("todo_title", todo.title)
            putExtra("todo_due_date", todo.dueDate ?: 0L)
            putExtra("todo_category", todo.category)
            putExtra("todo_firebase_id", todo.firebaseId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dueDate,
            pendingIntent
        )
    }

    private fun scheduleInexact(
        context: Context,
        alarmManager: AlarmManager,
        todo: Todo,
        dueDate: Long
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("todo_id", todo.id)
            putExtra("todo_title", todo.title)
            putExtra("todo_due_date", todo.dueDate ?: 0L)
            putExtra("todo_category", todo.category)
            putExtra("todo_firebase_id", todo.firebaseId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dueDate,
            pendingIntent
        )
    }

    fun cancel(context: Context, todoId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
