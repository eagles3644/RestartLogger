package com.dugan.restartlogger;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

/**
 * Created by Todd on 12/2/2014.
 */
public class MyWidgetProvider extends AppWidgetProvider {

    private static final String PREF_COUNTER = "restart_counter";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.widgetRebootCounter, String.valueOf(prefs.getInt(PREF_COUNTER, 0)));
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widgetRebootCounter, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.widgetRebootCounterLabel, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

}
