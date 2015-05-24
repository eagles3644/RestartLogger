package com.dugan.restartlogger;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Todd on 2/14/2015.
 */
public class RootChecker {


    private static boolean canExecuteSuCommand(){

        try{
            Runtime.getRuntime().exec("su");
            Log.d("RestartLogger:", "RootChecker-CanExecuteSuCommand = True");
            return true;
        }

        catch (IOException localIOException){
            Log.d("RestartLogger:", "RootChecker-CanExecuteSuCommand = False");
            return false;
        }

    }

    private static boolean superuserApkExists(){

        File file = new File("/system/app/Superuser.apk");
        Boolean bolFileExists = file.exists();
        Log.d("RestartLogger:", "RootChecker-superuserApkExists = " + bolFileExists);
        return bolFileExists;

    }

    private static boolean testKeyBuildExists(){

        Boolean bolExists = false;
        String buildTag = Build.TAGS;
        if ((buildTag != null) && (buildTag.contains("test-keys"))){
            bolExists = true;
        }
        Log.d("RestartLogger:", "RootChecker-testKeyBuildExists = " + bolExists);
        return bolExists;

    }

    public static boolean deviceRooted(){
        return canExecuteSuCommand() || superuserApkExists() || testKeyBuildExists();
    }

}
