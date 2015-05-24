package com.dugan.restartlogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Todd on 2/14/2015.
 */
public class ScheduleNextRebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Log.e("ScheduleRebootReceiver",":onReceive");
        android.os.SystemClock.sleep(2000);
        ScheduleFragment.getNextSchedule(context);
    }

}
