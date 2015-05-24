package com.dugan.restartlogger;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Todd on 12/2/2014.
 */
public class UpdateWidget {

    Context context;

    public UpdateWidget(Context c){
        this.context = c;
    }

    public void sendWidgetUpdate(){
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        Intent widgetIntent = new Intent(context, MyWidgetProvider.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
        context.sendBroadcast(widgetIntent);
    }

}
