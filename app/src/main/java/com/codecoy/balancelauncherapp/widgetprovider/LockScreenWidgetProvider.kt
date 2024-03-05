package com.codecoy.balancelauncherapp.widgetprovider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.codecoy.balancelauncherapp.R

class LockScreenWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        // Perform any updates to your lock screen widget here
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.lock_widget_layout)

            // Update your widget's content here
            // remoteViews.setTextViewText(R.id.widgetText, "Custom Lock Screen Widget")

            // You can add more customization as needed

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }
}
