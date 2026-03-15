package com.furkanyildirim.learningcompose.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.furkanyildirim.learningcompose.R

class TodoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            appWidgetIds.forEach { widgetId ->
                val views = RemoteViews(context.packageName, R.layout.widget_todo).apply {
                    setTextViewText(R.id.widget_title, context.getString(R.string.widget_title))

                    val quickAddIntent = Intent(context, QuickAddReceiver::class.java).apply {
                        action = QuickAddReceiver.ACTION_QUICK_ADD
                    }
                    val quickAddPendingIntent = PendingIntent.getBroadcast(
                        context,
                        widgetId,
                        quickAddIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    setOnClickPendingIntent(R.id.widget_quick_add_button, quickAddPendingIntent)
                }
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }
    }
}
