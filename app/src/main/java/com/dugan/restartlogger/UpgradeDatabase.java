package com.dugan.restartlogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Created by Todd on 5/4/2015.
 */
public class UpgradeDatabase extends AsyncTask<Context, Void, String> {

    private Activity myActivity;

    public UpgradeDatabase(Activity activity){
        myActivity = activity;
    }

    @Override
    protected String doInBackground(Context... params) {
        Context myContext = params[0];
        MySQLHelper mySQLHelper = new MySQLHelper(myContext);
        mySQLHelper.close();
        return null;
    }

    protected void onPostExecute(String result){
        Intent mainActivityIntent = new Intent(myActivity, MainActivity.class);
        myActivity.startActivityForResult(mainActivityIntent, 2);
        myActivity.finish();
    }
}
