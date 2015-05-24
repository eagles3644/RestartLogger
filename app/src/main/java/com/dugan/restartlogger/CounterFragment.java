package com.dugan.restartlogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment containing a simple view.
 */
public class CounterFragment extends Fragment {
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
    public static CounterFragment newInstance(int sectionNumber) {
        CounterFragment fragment = new CounterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public CounterFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final MySQLHelper mySQLHelper = new MySQLHelper(getActivity().getApplicationContext());
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        final View rootView = inflater.inflate(R.layout.fragment_counter, container, false);
        MyAlarmManager.scheduleNightlyCheck(rootView.getContext());
        final TextView rebootCounter = (TextView) rootView.findViewById(R.id.rebootCounter);
        final TextView lastReboot = (TextView) rootView.findViewById(R.id.lastReboot);
        final String[] lastRebootDatetime = new String[1];
        lastRebootDatetime[0] = getResources().getString(R.string.no_restarts_text);
        new DBTask().execute(new DBRunnable() {
            @Override
            public void executeDBTask() {
                int rebootCount = mySQLHelper.logRowCount();
                if (rebootCount > 0) {
                    lastRebootDatetime[0] = mySQLHelper.lastRebootDatetime();
                }
            }

            @Override
            public void postExecuteDBTask() {
                lastReboot.setText(lastRebootDatetime[0]);
            }
        });
        rebootCounter.setText(String.valueOf(prefs.getInt(PREF_COUNTER, 0)));
        rebootCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Toast.makeText(getActivity(), "Double tap to reset counter.", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Reset Counter");
                builder.setMessage("Are you sure you want to reset the counter to zero?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(PREF_COUNTER, 0);
                        editor.apply();
                        rebootCounter.setText(String.valueOf(prefs.getInt(PREF_COUNTER, 0)));
                        new UpdateWidget(getActivity().getApplicationContext()).sendWidgetUpdate();
                        Toast.makeText(getActivity(), "Counter reset to zero.", Toast.LENGTH_SHORT).show();
                        if(mySQLHelper.logRowCount() > 0) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                            builder2.setTitle("Delete Log Rows");
                            builder2.setMessage("Do you want to delete the corresponding log rows?");
                            builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DBTask().execute(new DBRunnable() {
                                        @Override
                                        public void executeDBTask() {
                                            mySQLHelper.deleteLogRows();
                                        }

                                        @Override
                                        public void postExecuteDBTask() {

                                        }
                                    });
                                }
                            });
                            builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder2.show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
        rebootCounter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        Button bootMenuButton = (Button) rootView.findViewById(R.id.bootMenuButton);
        bootMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuickBootMenu.class);
                startActivity(intent);
            }
        });
        Button addShortcutBtn = (Button) rootView.findViewById(R.id.addShortcutButton);
        addShortcutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                ComponentName comp = new ComponentName(getActivity().getPackageName(), ".QuickBootMenu");
                Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Reboot Menu");
                Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.ic_launcher);
                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
                getActivity().sendBroadcast(shortcut);
                getActivity().setResult(2);
                getActivity().finish();
            }
        });
        if(RootChecker.deviceRooted()){
            bootMenuButton.setVisibility(View.VISIBLE);
            addShortcutBtn.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}