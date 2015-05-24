package com.dugan.restartlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Todd on 11/30/2014.
 */
public class MySQLHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "RestartLogger.db";

    //Log Table Vars
    public static final String LOG_TABLE_NAME = "LOG";
    public static final String LOG_ID = "_id";
    public static final String LOG_ACTION = "action";
    public static final String LOG_INITIATOR = "initiator";
    public static final String LOG_SHUTDOWN_DATETIME = "shutdown_time";
    public static final String LOG_BOOT_DATETIME = "boot_time";
    public static final String LOG_DATETIME = "datetime";


    //Schedule Table Vars
    public static final String SCHEDULE_TABLE_NAME = "SCHEDULE";
    public static final String SCHEDULE_ID = "_id";
    public static final String SCHEDULE_NICKNAME = "NICKNAME";
    public static final String SCHEDULE_REPEAT = "REPEAT";
    public static final String SCHEDULE_SUNDAY = "SUNDAY";
    public static final String SCHEDULE_MONDAY = "MONDAY";
    public static final String SCHEDULE_TUESDAY = "TUESDAY";
    public static final String SCHEDULE_WEDNESDAY = "WEDNESDAY";
    public static final String SCHEDULE_THURSDAY = "THURSDAY";
    public static final String SCHEDULE_FRIDAY = "FRIDAY";
    public static final String SCHEDULE_SATURDAY = "SATURDAY";
    public static final String SCHEDULE_HOUR = "HOUR";
    public static final String SCHEDULE_MIN = "MIN";
    public static final String SCHEDULE_ENABLED = "ENABLED";
    public static final String SCHEDULE_DELETED = "DELETED";
    public static final String SCHEDULE_EXPANDED = "EXPANDED";
    public static final String SCHEDULE_REBOOT_TYPE = "TYPE";

    //Create Statement
    private static final String SQL_CREATE_LOG = "CREATE TABLE " + LOG_TABLE_NAME + " (" +
                                                        LOG_ID + " INTEGER PRIMARY KEY, " +
                                                        LOG_ACTION + " TEXT, " +
                                                        LOG_INITIATOR + " TEXT, " +
                                                        LOG_SHUTDOWN_DATETIME + " TEXT, " +
                                                        LOG_BOOT_DATETIME + " TEXT, " +
                                                        LOG_DATETIME + " TEXT)";

    private static final String SQL_CREATE_SCHEDULE = "CREATE TABLE " + SCHEDULE_TABLE_NAME + " (" +
                                                        SCHEDULE_ID + " INTEGER PRIMARY KEY, " +
                                                        SCHEDULE_NICKNAME + " TEXT, " + SCHEDULE_REPEAT
                                                        + " TEXT, " + SCHEDULE_SUNDAY +
                                                        " TEXT, " + SCHEDULE_MONDAY + " TEXT, " +
                                                        SCHEDULE_TUESDAY + " TEXT, " + SCHEDULE_WEDNESDAY +
                                                        " TEXT, " + SCHEDULE_THURSDAY + " TEXT, " +
                                                        SCHEDULE_FRIDAY + " TEXT, " + SCHEDULE_SATURDAY +
                                                        " TEXT, " + SCHEDULE_HOUR + " INTEGER, "
                                                        + SCHEDULE_MIN + " INTEGER, "  + SCHEDULE_ENABLED +
                                                        " TEXT, " + SCHEDULE_DELETED + " TEXT, " +
                                                        SCHEDULE_EXPANDED + " TEXT, " + SCHEDULE_REBOOT_TYPE + " TEXT)";

    public MySQLHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("RestartLogger:", "MySQLHelper-onCreate Version = " + DATABASE_VERSION);
        db.execSQL(SQL_CREATE_LOG);
        db.execSQL(SQL_CREATE_SCHEDULE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("RestartLogger:", "MySQLHelper-onUpgrade oldVersion = " + oldVersion + " newVersion = " + newVersion);
        Cursor logCursor;
        Cursor scheduleCursor;
        int logActionIndex;
        int logDtmIndex;
        int logShutdownDtmIndex;
        int logBootDtmIndex;
        int scheduleRebootTypeIndex;
        String alterSQL;
        String updateSQL;
        //Log Table Check
        try {
            logCursor = db.rawQuery("SELECT * FROM " + LOG_TABLE_NAME, null, null);
        }
        catch(Exception exception){
            db.execSQL(SQL_CREATE_LOG);
            logCursor = db.rawQuery("SELECT * FROM " + LOG_TABLE_NAME, null, null);
        }

        //Get Log Table Column Indexes
        logActionIndex = logCursor.getColumnIndex(LOG_ACTION);
        logDtmIndex = logCursor.getColumnIndex(LOG_DATETIME);
        logShutdownDtmIndex = logCursor.getColumnIndex(LOG_SHUTDOWN_DATETIME);
        logBootDtmIndex = logCursor.getColumnIndex(LOG_BOOT_DATETIME);
        Log.e("RestartLogger:", "MySQLHelper-onUpgrade boot_time index = " + logBootDtmIndex);

        //Log Table Upgrades
        if(logActionIndex == -1){
            alterSQL = "ALTER TABLE " + LOG_TABLE_NAME + " ADD COLUMN " + LOG_ACTION + " TEXT";
            db.execSQL(alterSQL);
            updateSQL = "UPDATE " + LOG_TABLE_NAME + " SET " + LOG_ACTION + "='Reboot'";
            db.execSQL(updateSQL);
        }
        if(logDtmIndex == -1) {
            alterSQL = "ALTER TABLE " + LOG_TABLE_NAME + " ADD COLUMN " + LOG_DATETIME + " TEXT";
            db.execSQL(alterSQL);
        }
        if(logShutdownDtmIndex == -1) {
            alterSQL = "ALTER TABLE " + LOG_TABLE_NAME + " ADD COLUMN " + LOG_SHUTDOWN_DATETIME + " TEXT";
            db.execSQL(alterSQL);
            updateSQL = "UPDATE " + LOG_TABLE_NAME + " SET " + LOG_SHUTDOWN_DATETIME + "='N/A'";
            db.execSQL(updateSQL);
        }
        if(logBootDtmIndex == -1){
            alterSQL = "ALTER TABLE " + LOG_TABLE_NAME + " ADD COLUMN " + LOG_BOOT_DATETIME + " TEXT";
            db.execSQL(alterSQL);
            updateSQL = "UPDATE " + LOG_TABLE_NAME + " SET " + LOG_BOOT_DATETIME + "=" + LOG_DATETIME;
            db.execSQL(updateSQL);
        }


        //Schedule Table Check
        try {
            scheduleCursor = db.rawQuery("SELECT * FROM " + SCHEDULE_TABLE_NAME, null, null);
        }
        catch(Exception exception){
            db.execSQL(SQL_CREATE_SCHEDULE);
            scheduleCursor = db.rawQuery("SELECT * FROM " + SCHEDULE_TABLE_NAME, null, null);
        }

        //Get Schedule Table Column Indexes
        scheduleRebootTypeIndex = scheduleCursor.getColumnIndex(SCHEDULE_REBOOT_TYPE);

        //Schedule Table Upgrades
        if(scheduleRebootTypeIndex == -1){
            alterSQL = "ALTER TABLE " + SCHEDULE_TABLE_NAME + " ADD COLUMN " + SCHEDULE_REBOOT_TYPE + " TEXT";
            db.execSQL(alterSQL);
            updateSQL = "UPDATE " + SCHEDULE_TABLE_NAME + " SET " + SCHEDULE_REBOOT_TYPE + "='Reboot'";
            db.execSQL(updateSQL);
        }

        logCursor.close();
        scheduleCursor.close();
    }

    public int logRowCount(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                LOG_ID,
                LOG_ACTION,
                LOG_INITIATOR,
                LOG_BOOT_DATETIME
        };
        Cursor cursor = db.query(LOG_TABLE_NAME, projection, null, null, null, null, null);
        cursor.moveToLast();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deleteLogRows(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(LOG_TABLE_NAME, null, null);
    }

    public void deleteLogByID(int ID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + LOG_TABLE_NAME + " WHERE " + LOG_ID + "=" + ID;
        db.execSQL(query);
    }

    public String lastRebootDatetime(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                LOG_ID,
                LOG_ACTION,
                LOG_INITIATOR,
                LOG_BOOT_DATETIME
        };
        String order = LOG_ID + " DESC";
        Cursor cursor = db.query(LOG_TABLE_NAME, projection, null, null, null, null, order);
        cursor.moveToLast();
        cursor.moveToFirst();
        String lastRebootDtm = cursor.getString(cursor.getColumnIndex(LOG_BOOT_DATETIME));
        cursor.close();
        return lastRebootDtm;
    }

    public Cursor logCursor(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                LOG_ID,
                LOG_ACTION,
                LOG_INITIATOR,
                LOG_SHUTDOWN_DATETIME,
                LOG_BOOT_DATETIME
        };
        String order = LOG_ID + " DESC";
        Cursor cursor = db.query(LOG_TABLE_NAME, projection, null, null, null, null, order);
        cursor.moveToLast();
        cursor.moveToFirst();
        return cursor;
    }

    public boolean scheduleExists(int hour, int min){
        Boolean exists = false;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + SCHEDULE_ID + " FROM " + SCHEDULE_TABLE_NAME + " WHERE "
                + SCHEDULE_HOUR + "=" + hour + " AND " + SCHEDULE_MIN + "=" + min;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        if (count > 0){
            exists = true;
        }
        return exists;
    }

    public void addSchedule(int hour, int min){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_REPEAT, "N");
        values.put(SCHEDULE_SUNDAY, "N");
        values.put(SCHEDULE_MONDAY, "N");
        values.put(SCHEDULE_TUESDAY, "N");
        values.put(SCHEDULE_WEDNESDAY, "N");
        values.put(SCHEDULE_THURSDAY, "N");
        values.put(SCHEDULE_FRIDAY, "N");
        values.put(SCHEDULE_SATURDAY, "N");
        values.put(SCHEDULE_HOUR, hour);
        values.put(SCHEDULE_MIN, min);
        values.put(SCHEDULE_DELETED, "N");
        values.put(SCHEDULE_EXPANDED, "N");
        values.put(SCHEDULE_ENABLED, "N");
        values.put(SCHEDULE_REBOOT_TYPE, "Full Reboot");
        db.insert(SCHEDULE_TABLE_NAME, null, values);
    }

    public Cursor getSchedules(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + SCHEDULE_TABLE_NAME + " WHERE " + SCHEDULE_DELETED + "='N'";
        return db.rawQuery(query, null);
    }

    public Cursor getScheduleById(int id){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + SCHEDULE_TABLE_NAME + " WHERE " + SCHEDULE_DELETED + "='N' AND " + SCHEDULE_ID + "=" + id;
        return db.rawQuery(query, null);
    }

    public void upScheduleSunday(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_SUNDAY, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleMonday(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_MONDAY, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleTuesday(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_TUESDAY, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleWednesday(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_WEDNESDAY, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleThursday(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_THURSDAY, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleFriday(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_FRIDAY, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleSaturday(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_SATURDAY, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleHour(int value, int id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_HOUR, value);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleMin(int value, int id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_MIN, value);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleRepeatInd(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_REPEAT, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleExpandInd(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_EXPANDED, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleDeleted(Boolean value, int id) {
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_DELETED, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleEnabledInd(Boolean value, int id){
        SQLiteDatabase db = getWritableDatabase();
        String convertedValue = "N";
        if(value){
            convertedValue = "Y";
        }
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_ENABLED, convertedValue);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void upScheduleRebootType(String type, int id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCHEDULE_REBOOT_TYPE, type);
        db.update(SCHEDULE_TABLE_NAME, values, SCHEDULE_ID + "=" + id, null);
    }

    public void deleteDeletedSchedules(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SCHEDULE_TABLE_NAME, SCHEDULE_DELETED + "='Y'", null);
    }

    public int scheduleCount(){
        Cursor cursor = getSchedules();
        cursor.moveToLast();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Cursor getNextReboot(String dayOfWeek, int hour, int min){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + SCHEDULE_TABLE_NAME + " WHERE " + SCHEDULE_DELETED + "='N'" +
                " AND " + SCHEDULE_ENABLED + "='Y' AND " + dayOfWeek + "='Y' AND " +
                SCHEDULE_HOUR + ">=" + hour + " AND " + SCHEDULE_MIN + ">=" + min
                + " AND " + SCHEDULE_REPEAT + "='Y' OR " + SCHEDULE_HOUR + ">=" + hour + " AND " + SCHEDULE_MIN + ">=" + min
                + " AND " + SCHEDULE_REPEAT + "='N' AND " + SCHEDULE_ENABLED + "='Y'" + " ORDER BY "
                + SCHEDULE_HOUR + ", " + SCHEDULE_MIN + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToLast();
        return cursor;
    }

    public int getNextRebootCount(String dayOfWeek, int hour, int min){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + SCHEDULE_TABLE_NAME + " WHERE " + SCHEDULE_DELETED + "='N'" +
                " AND " + SCHEDULE_ENABLED + "='Y' AND " + dayOfWeek + "='Y' AND " +
                SCHEDULE_HOUR + ">=" + hour + " AND " + SCHEDULE_MIN + ">=" + min
                + " AND " + SCHEDULE_REPEAT + "='Y' OR " + SCHEDULE_HOUR + ">=" + hour + " AND " + SCHEDULE_MIN + ">=" + min
                + " AND " + SCHEDULE_REPEAT + "='N' AND " + SCHEDULE_ENABLED + "='Y'" + " ORDER BY "
                + SCHEDULE_HOUR + ", " + SCHEDULE_MIN + " ASC";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToLast();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

}
