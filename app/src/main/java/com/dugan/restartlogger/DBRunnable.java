package com.dugan.restartlogger;

import android.os.AsyncTask;

/**
 * Created by Todd on 5/16/2015.
 */
public interface DBRunnable {
    void executeDBTask();
    void postExecuteDBTask();
}

class DBTask extends AsyncTask<DBRunnable, Void, DBRunnable> {
    @Override
    protected DBRunnable doInBackground(DBRunnable... runnables){
        runnables[0].executeDBTask();
        return runnables[0];
    }

    @Override
    protected void onPostExecute(DBRunnable runnable){
        runnable.postExecuteDBTask();
    }
}
