package com.dugan.restartlogger;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;

/**
 * Created by Todd on 12/1/2014.
 */
public class RecordRestart {

    Context context;

    public static final String PREF_COUNTER = "restart_counter";

    public RecordRestart(Context c){
        this.context = c;
    }

    public void record() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_COUNTER, prefs.getInt(PREF_COUNTER, 0) + 1);
        editor.apply();
        long shutdownMili = prefs.getLong(ShutdownReceiver.PREF_SHUTDOWN_MILI, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        String lastRebootDatetime = sdf.format(System.currentTimeMillis());
        String shutdownDatetime = sdf.format(shutdownMili);
        if(shutdownMili == 0){
            shutdownDatetime = "NA";
        }
        String action = prefs.getString(ShutdownReceiver.PREF_SHUTDOWN_TYPE, "Reboot");
        MySQLHelper mySQLHelper = new MySQLHelper(context);
        SQLiteDatabase db = mySQLHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLHelper.LOG_ACTION, action);
        values.put(MySQLHelper.LOG_SHUTDOWN_DATETIME, shutdownDatetime);
        values.put(MySQLHelper.LOG_BOOT_DATETIME, lastRebootDatetime);
        db.insert(MySQLHelper.LOG_TABLE_NAME, null, values);
    }

}
