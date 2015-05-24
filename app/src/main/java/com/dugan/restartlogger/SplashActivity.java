package com.dugan.restartlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        setFinishOnTouchOutside(false);
        TextView splashText = (TextView) findViewById(R.id.SplashText);
        String textToDisplay = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String DB_VERSION_PREF = "database_version_pref";
        int dbVersionPref = prefs.getInt(DB_VERSION_PREF, 0);
        String ALREADY_INSTALLED = "already_installed";
        Boolean alreadyInstalled = prefs.getBoolean(ALREADY_INSTALLED, false);
        int dbVersionSQL = MySQLHelper.DATABASE_VERSION;
        if(!alreadyInstalled || dbVersionSQL > dbVersionPref){
            if(!alreadyInstalled) {
                textToDisplay = "Installing, please wait...";
            } else if(dbVersionSQL > dbVersionPref){
                textToDisplay = "Upgrading, please wait...";
            }
            splashText.setText(textToDisplay);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(ALREADY_INSTALLED, true);
            editor.putInt(DB_VERSION_PREF, dbVersionSQL);
            editor.apply();
            new UpgradeDatabase(this).execute(this);
        } else {
            Intent noActionIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivityForResult(noActionIntent, 2);
        }
    }

    @Override
    public void onBackPressed(){
        Log.e("SplashActivity:", "onBackPressed-Back button was pressed.");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==2){
            finish();
        }
    }
}
