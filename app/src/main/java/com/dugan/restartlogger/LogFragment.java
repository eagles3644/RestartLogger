package com.dugan.restartlogger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A fragment containing a simple view.
 */
public class LogFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static LogCursorAdapter logCursorAdapter;
    private static Cursor logCursor;
    private static ListView logListView;
    private static RelativeLayout logsExistView;
    private static RelativeLayout noLogsExistView;


    public static LogFragment newInstance(int sectionNumber) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public LogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        logsExistView = (RelativeLayout) rootView.findViewById(R.id.logTableView);
        noLogsExistView = (RelativeLayout) rootView.findViewById(R.id.noLogView);
        final MySQLHelper mySQLHelper = new MySQLHelper(getActivity());
        if (mySQLHelper.logRowCount() == 0) {
            logsExistView.setVisibility(View.GONE);
            noLogsExistView.setVisibility(View.VISIBLE);
        } else {
            logsExistView.setVisibility(View.VISIBLE);
            noLogsExistView.setVisibility(View.GONE);
            logListView = (ListView) rootView.findViewById(R.id.logList);
            new DBTask().execute(new DBRunnable() {
                @Override
                public void executeDBTask() {
                    logCursor = mySQLHelper.logCursor();
                    logCursorAdapter = new LogCursorAdapter(rootView.getContext(), logCursor, 0);
                }

                @Override
                public void postExecuteDBTask() {
                    logListView.setAdapter(logCursorAdapter);
                }
            });
            logListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            logListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    int checkedCount = logListView.getCheckedItemCount();
                    mode.setTitle(checkedCount + " Selected");
                    TextView logID = (TextView) logListView.getChildAt(position).findViewById(R.id.logRowID);
                    int intLogID = Integer.parseInt(logID.getText().toString());
                    Log.e("LogID = ", "" + intLogID);
                    logCursorAdapter.selectItem(intLogID, checked);
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.main_delete, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            new DBTask().execute(new DBRunnable() {
                                @Override
                                public void executeDBTask() {
                                    SparseBooleanArray selectedIds = logCursorAdapter.getSelectedIds();
                                    for (int i = (selectedIds.size() - 1); i >= 0; i--) {
                                        if (selectedIds.valueAt(i)) {
                                            MySQLHelper db = new MySQLHelper(rootView.getContext());
                                            db.deleteLogByID(selectedIds.keyAt(i));
                                        }
                                    }
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt(RecordRestart.PREF_COUNTER, prefs.getInt(RecordRestart.PREF_COUNTER, selectedIds.size())-selectedIds.size());
                                    editor.apply();
                                }

                                @Override
                                public void postExecuteDBTask() {
                                    mode.finish();
                                    refreshLogCursor(rootView.getContext());
                                }
                            });
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    logCursorAdapter.removeSelection();
                }
            });
            logListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    refreshLogCursor(rootView.getContext());
                }
            });
        }
        return rootView;
    }

    public static void refreshLogCursor(final Context context){
        final Cursor[] newCursor = new Cursor[1];
        new DBTask().execute(new DBRunnable() {
            @Override
            public void executeDBTask() {
                MySQLHelper db = new MySQLHelper(context);
                newCursor[0] = db.logCursor();
            }

            @Override
            public void postExecuteDBTask() {
                logCursorAdapter.swapCursor(newCursor[0]);
                MySQLHelper db = new MySQLHelper(context);
                if (db.logRowCount() == 0) {
                    logsExistView.setVisibility(View.GONE);
                    noLogsExistView.setVisibility(View.VISIBLE);
                }
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