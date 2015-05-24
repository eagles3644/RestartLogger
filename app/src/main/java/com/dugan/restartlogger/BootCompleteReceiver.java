package com.dugan.restartlogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import java.util.Calendar;

/**
 * Created by Todd on 11/30/2014.
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new RecordRestart(context).record();
        new UpdateWidget(context).sendWidgetUpdate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notify = prefs.getBoolean("notifications", true);
        if (notify){
            new SendNotification(context).notifyOfRestart();
        }
        MyAlarmManager.scheduleNightlyCheck(context);
        MyAlarmManager.cancelAlarm(context, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        String day = "SUNDAY";
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                day = "SUNDAY";
                break;
            case Calendar.MONDAY:
                day = "MONDAY";
                break;
            case Calendar.TUESDAY:
                day = "TUESDAY";
                break;
            case Calendar.WEDNESDAY:
                day = "WEDNESDAY";
                break;
            case Calendar.THURSDAY:
                day = "THURSDAY";
                break;
            case Calendar.FRIDAY:
                day = "FRIDAY";
                break;
            case Calendar.SATURDAY:
                day = "SATURDAY";
                break;
        }
        MySQLHelper db = new MySQLHelper(context);
        int count = db.getNextRebootCount(day, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        if(count > 0) {
            Cursor cursor = db.getNextReboot(day, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            cursor.moveToFirst();
            if (prefs.getInt("NextAlarm", 0) != cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_ID))) {
                MyAlarmManager.startAlarm(context, cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_HOUR)),
                        cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_MIN)));
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("NextAlarm", cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_ID)));
                editor.apply();
            }
        }
    }
}
