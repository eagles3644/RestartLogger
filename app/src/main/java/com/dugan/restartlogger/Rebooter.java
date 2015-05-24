package com.dugan.restartlogger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Todd on 2/14/2015.
 */
public class Rebooter {

    public static void rebootController(Context context, String action){

        //Build intent for rebooting activity
        Intent intent = new Intent(context, RebootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("Action", action);

        //Send shutdown broadcast if necessary
        if(!action.equals("Hot Reboot")){
            try{
                Runtime.getRuntime().exec("su -c am broadcast android.intent.action.ACTION_SHUTDOWN");
                Log.d("RestartLogger:", "Sending Shutdown Broadcast Success");
            }

            catch (IOException localIOException){
                Log.d("RestartLogger:", "Sending Shutdown Broadcast Failed");
            }
        }

        //Show Rebooting Activity
        context.startActivity(intent);
    }

    public static void reboot(Context context, String option){

        try{
            ShutdownReceiver.recordShutdown(context, "Reboot" + option);
            if(!option.equals("") && !option.isEmpty()){
                option = option.toLowerCase();
            }
            Runtime.getRuntime().exec("su -c am broadcast android.intent.action.ACTION_SHUTDOWN && reboot" + option);
            Log.d("RestartLogger:", "Rebooter-Reboot" + option + " Success");
        }

        catch (IOException localIOException){
            Log.d("RestartLogger:", "Rebooter-Reboot" + option + " Failed");
        }
    }

    public static void shutdown(Context context){

        try{
            ShutdownReceiver.recordShutdown(context, "Shutdown");
            Runtime.getRuntime().exec("su -c am broadcast android.intent.action.ACTION_SHUTDOWN && reboot -p");
            Log.d("RestartLogger:", "Rebooter-Shutdown Success");
        }

        catch (IOException localIOException){
            Log.d("RestartLogger:", "Rebooter-Shutdown Failed");
        }

    }

    public static void hotReboot(Context context){

        try{
            ShutdownReceiver.recordShutdown(context, "Hot Reboot");
            Runtime.getRuntime().exec("su -c killall zygote");
            Log.d("RestartLogger:", "Rebooter-SoftReboot Success");
        }

        catch (IOException localIOException){
            Log.d("RestartLogger:", "Rebooter-SoftReboot Failed");
        }

    }

}
