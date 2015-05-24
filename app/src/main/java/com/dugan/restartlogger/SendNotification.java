package com.dugan.restartlogger;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by Todd on 12/2/2014.
 */
public class SendNotification {

    Context context;

    private static final int RESTART_NOTIF_ID = 3644;

    public SendNotification(Context c){
        this.context = c;
    }

    public void notifyOfRestart(){
        MySQLHelper mySQLHelper = new MySQLHelper(context);
        String lastRestartDatetime = mySQLHelper.lastRebootDatetime();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_simple_icon)
                .setContentTitle("Restart Detected!")
                .setContentText("Your device was restarted at " + lastRestartDatetime)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(RESTART_NOTIF_ID, builder.build());
    }

}
