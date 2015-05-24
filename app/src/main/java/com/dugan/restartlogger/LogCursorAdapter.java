package com.dugan.restartlogger;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class LogCursorAdapter extends CursorAdapter {

    private SparseBooleanArray selectedItemIds = new SparseBooleanArray();

    public LogCursorAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.log_row, null);
        ViewHolder holder = new ViewHolder();
        holder.id = (TextView) view.findViewById(R.id.logRowID);
        holder.action = (TextView) view.findViewById(R.id.logRowAction);
        holder.shutdownTime = (TextView) view.findViewById(R.id.logRowShutdownDatetime);
        holder.bootTime = (TextView) view.findViewById(R.id.logRowBootDatetime);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View convertView, final Context context, final Cursor cursor) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        int logRowID = cursor.getInt(cursor.getColumnIndex(MySQLHelper.LOG_ID));
        String logRowAction = cursor.getString(cursor.getColumnIndex(MySQLHelper.LOG_ACTION));
        String logRowShutdown = cursor.getString(cursor.getColumnIndex(MySQLHelper.LOG_SHUTDOWN_DATETIME));
        String logRowBoot = cursor.getString(cursor.getColumnIndex(MySQLHelper.LOG_BOOT_DATETIME));
        holder.id.setText(Integer.toString(logRowID));
        holder.action.setText(logRowAction);
        holder.shutdownTime.setText(logRowShutdown);
        holder.bootTime.setText(logRowBoot);
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }

    public void selectItem(int id, boolean value){
        if (value){
            selectedItemIds.put(id, true);
        } else {
            selectedItemIds.delete(id);
        }
    }

    public void removeSelection(){
        selectedItemIds = new SparseBooleanArray();
    }

    public int getSelectedCount(){
        return selectedItemIds.size();
    }

    public SparseBooleanArray getSelectedIds(){
        return selectedItemIds;
    }

    static class ViewHolder{
        TextView id;
        TextView action;
        TextView shutdownTime;
        TextView bootTime;
    }
}
