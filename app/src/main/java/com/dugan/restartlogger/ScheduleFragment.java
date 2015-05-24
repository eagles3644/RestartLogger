package com.dugan.restartlogger;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * A fragment containing a simple view.
 */
public class ScheduleFragment extends Fragment {
    private static final String PREF_COUNTER = "restart_counter";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    private static ScheduleCursorAdapter scheduleCursorAdapter;
    private static Cursor scheduleCursor;
    private static ListView scheduleListView;
    private static RelativeLayout noScheduleView;

    public static ScheduleFragment newInstance(int sectionNumber) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        Button addButton = (Button) rootView.findViewById(R.id.addSchedule);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
                        final MySQLHelper db = new MySQLHelper(rootView.getContext());
                        final Boolean[] bolExists = new Boolean[1];
                        new DBTask().execute(new DBRunnable() {
                            @Override
                            public void executeDBTask() {
                                bolExists[0] = db.scheduleExists(hourOfDay, minute);
                            }

                            @Override
                            public void postExecuteDBTask() {
                                if(bolExists[0]){
                                    Toast.makeText(rootView.getContext(), "A scheduled restart already exists for the selected time.", Toast.LENGTH_LONG).show();
                                } else {
                                    db.addSchedule(hourOfDay, minute);
                                    refreshScheduleCursor(rootView.getContext());
                                    getNextSchedule(rootView.getContext());
                                }
                            }
                        });
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE)-1;
                if(min == -1){
                    hour = hour-1;
                    min = 59;
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(rootView.getContext(), timeSetListener, hour, min, false);
                timePickerDialog.show();
            }
        });
        scheduleListView = (ListView) rootView.findViewById(R.id.scheduleList);
        new DBTask().execute(new DBRunnable() {
            @Override
            public void executeDBTask() {
                MySQLHelper db = new MySQLHelper(rootView.getContext());
                scheduleCursor = db.getSchedules();
                scheduleCursorAdapter = new ScheduleCursorAdapter(
                        rootView.getContext(),
                        scheduleCursor,
                        0);
            }

            @Override
            public void postExecuteDBTask() {
                scheduleListView.setAdapter(scheduleCursorAdapter);
            }
        });
        noScheduleView = (RelativeLayout) rootView.findViewById(R.id.noScheduleView);
        MySQLHelper db = new MySQLHelper(rootView.getContext());
        if(db.scheduleCount() == 0){
            noScheduleView.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    public static void refreshScheduleCursor(final Context context){
        final Cursor[] newCursor = new Cursor[1];
        new DBTask().execute(new DBRunnable() {
            @Override
            public void executeDBTask() {
                MySQLHelper db = new MySQLHelper(context);
                newCursor[0] = db.getSchedules();
            }

            @Override
            public void postExecuteDBTask() {
                MySQLHelper db = new MySQLHelper(context);
                if(db.scheduleCount() == 0){
                    noScheduleView.setVisibility(View.VISIBLE);
                } else {
                    noScheduleView.setVisibility(View.GONE);
                }
                scheduleCursorAdapter.swapCursor(newCursor[0]);
            }
        });
    }

    public static void getNextSchedule(final Context context){
        MyAlarmManager.cancelAlarm(context, 0);
        MyAlarmManager.scheduleNightlyCheck(context);
        final MySQLHelper db = new MySQLHelper(context);
        final int[] count = new int[1];
        new DBTask().execute(new DBRunnable() {
            @Override
            public void executeDBTask() {
                final Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                String day = "SUNDAY";
                switch (calendar.get(Calendar.DAY_OF_WEEK)){
                    case Calendar.SUNDAY:
                        day = "SUNDAY";
                        break;
                    case Calendar.MONDAY:
                        day = "MONDAY";
                        break;
                    case Calendar.TUESDAY:
                        day = "TUESDAY";
                        break;
                    case Calendar.WEDNESDAY:
                        day = "WEDNESDAY";
                        break;
                    case Calendar.THURSDAY:
                        day = "THURSDAY";
                        break;
                    case Calendar.FRIDAY:
                        day = "FRIDAY";
                        break;
                    case Calendar.SATURDAY:
                        day = "SATURDAY";
                        break;
                }
                count[0] = db.getNextRebootCount(day, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                if(count[0] > 0) {
                    Cursor cursor = db.getNextReboot(day, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    cursor.moveToFirst();
                    MyAlarmManager.startAlarm(context, cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_HOUR)),
                            cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_MIN)));
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("NextAlarm", cursor.getInt(cursor.getColumnIndex(MySQLHelper.SCHEDULE_ID)));
                    editor.apply();
                }
            }

            @Override
            public void postExecuteDBTask() {
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}