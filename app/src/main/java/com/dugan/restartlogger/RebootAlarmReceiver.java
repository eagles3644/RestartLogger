package com.dugan.restartlogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

/**
 * Created by Todd on 2/14/2015.
 */
public class RebootAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        MyAlarmManager.cancelAlarm(context, 0);
        int id = prefs.getInt("NextAlarm", 0);
        if(id != 0){
            MySQLHelper db = new MySQLHelper(context);
            Cursor cursor = db.getScheduleById(id);
            cursor.moveToLast();
            String rebootType = cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_REBOOT_TYPE));
            if(cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_REPEAT)).equals("N")){
                db.upScheduleEnabledInd(false, id);
            }
            db.close();
            if(rebootType.equals("Full Reboot")){
                Rebooter.reboot(context, "");
            } else if(rebootType.equals("Hot Reboot")) {
                Rebooter.hotReboot(context);
            }
        }
    }

}
