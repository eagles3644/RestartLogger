package com.dugan.restartlogger;

/**
 * Created by Todd on 2/14/2015.
 */

import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

/**
 * Created by Todd on 1/4/2015.
 */
public class ScheduleCursorAdapter extends CursorAdapter {

    public ScheduleCursorAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.schedule_row, null);
        ViewHolder holder = new ViewHolder();
        holder.titleView = (RelativeLayout) view.findViewById(R.id.scheduleTitleView);
        holder.contentView = (RelativeLayout) view.findViewById(R.id.scheduleContentView);
        holder.time = (TextView) view.findViewById(R.id.scheduleTime);
        holder.repeat = (CheckBox) view.findViewById(R.id.scheduleRepeat);
        holder.contentShow = (ImageView) view.findViewById(R.id.scheduleContentShow);
        holder.daysOfWeek = (RelativeLayout) view.findViewById(R.id.scheduleDaysView);
        holder.sunday = (ToggleButton) view.findViewById(R.id.scheduleSunday);
        holder.monday = (ToggleButton) view.findViewById(R.id.scheduleMonday);
        holder.tuesday = (ToggleButton) view.findViewById(R.id.scheduleTuesday);
        holder.wednesday = (ToggleButton) view.findViewById(R.id.scheduleWednesday);
        holder.thursday = (ToggleButton) view.findViewById(R.id.scheduleThursday);
        holder.friday = (ToggleButton) view.findViewById(R.id.scheduleFriday);
        holder.saturday = (ToggleButton) view.findViewById(R.id.scheduleSaturday);
        holder.delete = (ImageView) view.findViewById(R.id.scheduleDelete);
        holder.enabled = (Switch) view.findViewById(R.id.scheduleEnabled);
        holder.fullReboot = (RadioButton) view.findViewById(R.id.radioFullReboot);
        holder.hotReboot = (RadioButton) view.findViewById(R.id.radioHotReboot);
        holder.rebootTypeRadioGroup = (RadioGroup) view.findViewById(R.id.rebootTypeRadioGroup);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View convertView, final Context context, final Cursor cursor) {
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final MySQLHelper db = new MySQLHelper(context);
        final int id = cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_ID));
        Boolean bolRepeat = false;
        if (cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_REPEAT)).equals("Y")) {
            bolRepeat = true;
            holder.daysOfWeek.setVisibility(View.VISIBLE);
        }
        holder.repeat.setChecked(bolRepeat);
        holder.repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleRepeatInd(isChecked, id);
                ScheduleFragment.getNextSchedule(context);
                if (isChecked) {
                    CustomAnimations.expand(context, holder.daysOfWeek);
                } else {
                    CustomAnimations.collapse(context, holder.daysOfWeek);
                }
            }
        });
        String rebootType = cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_REBOOT_TYPE));
        if(rebootType.equals("Full Reboot")){
            holder.fullReboot.setChecked(true);
            holder.hotReboot.setChecked(false);
        } else if(rebootType.equals("Hot Reboot")){
            holder.fullReboot.setChecked(false);
            holder.hotReboot.setChecked(true);
        }
        holder.rebootTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioFullReboot){
                    db.upScheduleRebootType("Full Reboot", id);
                } else if(checkedId == R.id.radioHotReboot){
                    db.upScheduleRebootType("Hot Reboot", id);
                }
                ScheduleFragment.refreshScheduleCursor(context);
            }
        });
        Boolean bolSunday = false;
        if (cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_SUNDAY)).equals("Y")) {
            bolSunday = true;
        }
        holder.sunday.setChecked(bolSunday);
        holder.sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleSunday(isChecked, id);
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        Boolean bolMonday = false;
        if (cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_MONDAY)).equals("Y")) {
            bolMonday = true;
        }
        holder.monday.setChecked(bolMonday);
        holder.monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleMonday(isChecked, id);
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        Boolean bolTuesday = false;
        if (cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_TUESDAY)).equals("Y")) {
            bolTuesday = true;
        }
        holder.tuesday.setChecked(bolTuesday);
        holder.tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleTuesday(isChecked, id);
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        Boolean bolWednesday = false;
        if (cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_WEDNESDAY)).equals("Y")) {
            bolWednesday = true;
        }
        holder.wednesday.setChecked(bolWednesday);
        holder.wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleWednesday(isChecked, id);
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        Boolean bolThursday = false;
        if (cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_THURSDAY)).equals("Y")) {
            bolThursday = true;
        }
        holder.thursday.setChecked(bolThursday);
        holder.thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleThursday(isChecked, id);
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        Boolean bolFriday = false;
        if (cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_FRIDAY)).equals("Y")) {
            bolFriday = true;
        }
        holder.friday.setChecked(bolFriday);
        holder.friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleFriday(isChecked, id);
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        Boolean bolSaturday = false;
        if (cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_SATURDAY)).equals("Y")) {
            bolSaturday = true;
        }
        holder.saturday.setChecked(bolSaturday);
        holder.saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleSaturday(isChecked, id);
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        final int hour = cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_HOUR));
        final int min = cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_MIN));
        String strMin = "00" + min;
        String time;
        if(hour == 99 && min == 99){
            time = "12:00 AM";
        } else if(hour < 12){
            if(hour == 0){
                time = "12" + ":" + strMin.substring(strMin.length()-2) + " AM";
            } else {
                time = hour + ":" + strMin.substring(strMin.length()-2) + " AM";
            }
        } else {
            if(hour == 12){
                time = hour + ":" + strMin.substring(strMin.length()-2) + " PM";
            } else {
                time = hour-12 + ":" + strMin.substring(strMin.length()-2) + " PM";
            }
        }
        holder.time.setText(time);
        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        db.upScheduleHour(hourOfDay, id);
                        db.upScheduleMin(minute, id);
                        ScheduleFragment.refreshScheduleCursor(context);
                        ScheduleFragment.getNextSchedule(context);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, timeSetListener, hour, min,false);
                timePickerDialog.show();
            }
        });
        holder.contentShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.contentView.getVisibility() == View.VISIBLE) {
                    CustomAnimations.collapse(context, holder.contentView);
                    CustomAnimations.rotate180(context, holder.contentShow);
                    db.upScheduleExpandInd(false, id);
                } else {
                    CustomAnimations.expand(context, holder.contentView);
                    CustomAnimations.rotate180(context, holder.contentShow);
                    db.upScheduleExpandInd(true, id);
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.upScheduleDeleted(true, id);
                db.deleteDeletedSchedules();
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        Boolean enabled = false;
        if(cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_ENABLED)).equals("Y")){
            enabled = true;
        }
        holder.enabled.setChecked(enabled);
        holder.enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.upScheduleEnabledInd(isChecked, id);
                ScheduleFragment.refreshScheduleCursor(context);
                ScheduleFragment.getNextSchedule(context);
            }
        });
        if(cursor.getString(cursor.getColumnIndex(MySQLHelper.SCHEDULE_EXPANDED)).equals("Y")){
            holder.contentView.setVisibility(View.VISIBLE);
            holder.contentShow.setRotation(180);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }

    static class ViewHolder{
        RelativeLayout contentView;
        RelativeLayout titleView;
        ImageView contentShow;
        TextView time;
        CheckBox repeat;
        RelativeLayout daysOfWeek;
        ToggleButton sunday;
        ToggleButton monday;
        ToggleButton tuesday;
        ToggleButton wednesday;
        ToggleButton thursday;
        ToggleButton friday;
        ToggleButton saturday;
        ImageView delete;
        Switch enabled;
        RadioButton fullReboot;
        RadioButton hotReboot;
        RadioGroup rebootTypeRadioGroup;
    }
}
