package com.dugan.restartlogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;


public class RebootActivity extends Activity {

    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_reboot);
        Intent intent = getIntent();
        action = intent.getStringExtra("Action");
        String textToDisplay = "Preparing to perform " + action + "...";
        TextView rebootingText = (TextView) findViewById(R.id.RebootingText);
        rebootingText.setText(textToDisplay);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    @Override
    public void onBackPressed(){
        Log.e("RebootActivity:", "onBackPressed-Back button was pressed.");
    }

    @Override
    public void finish(){
        super.finish();
        switch (action) {
            case "Shutdown":
                Rebooter.shutdown(this);
                break;
            case "Reboot":
                Rebooter.reboot(this, "");
                break;
            case "Reboot Recovery":
                Rebooter.reboot(this, " Recovery");
                break;
            case "Reboot Bootloader":
                Rebooter.reboot(this, " Bootloader");
                break;
            case "Hot Reboot":
                Rebooter.hotReboot(this);
                break;
        }
    }

}
