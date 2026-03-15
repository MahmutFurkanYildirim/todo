package com.furkanyildirim.learningcompose.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.furkanyildirim.learningcompose.R
import com.furkanyildirim.learningcompose.data.local.TodoDatabase
import com.furkanyildirim.learningcompose.data.model.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuickAddReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_QUICK_ADD) return

        CoroutineScope(Dispatchers.IO).launch {
            val dao = TodoDatabase.getDatabase(context).todoDao()
            val title = context.getString(R.string.widget_quick_add_default_title)
            dao.addTodo(Todo(title = title))

            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, TodoWidgetProvider::class.java)
            )
            TodoWidgetProvider.updateWidgets(context, manager, ids)
        }
    }

    companion object {
        const val ACTION_QUICK_ADD = "com.furkanyildirim.learningcompose.ACTION_WIDGET_QUICK_ADD"
    }
}
