package com.dugan.restartlogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Todd on 3/7/2015.
 */
public class ShutdownReceiver extends BroadcastReceiver {

    //public static final String PREF_SHUTDOWN_DATETIME = "shutdown_datetime";
    public static final String PREF_SHUTDOWN_MILI = "shutdown_mili";
    public static final String PREF_SHUTDOWN_TYPE = "shutdown_type";

    @Override
    public void onReceive(Context context, Intent intent){
        //Toast.makeText(context, "Shutdown Broadcast Recieved", Toast.LENGTH_SHORT).show();
        //long currentMili = System.currentTimeMillis();
        //SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        //String shutdownDatetime = sdf.format(currentMili-shutdownTimeMili);
        recordShutdown(context, "Shutdown");
    }

    public static void recordShutdown(Context context, String type){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREF_SHUTDOWN_MILI, System.currentTimeMillis());
        editor.putString(PREF_SHUTDOWN_TYPE, type);
        editor.apply();
        Log.d("Recorded shutdown mili", "");
    }

}
