package com.dugan.restartlogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A fragment containing a simple view.
 */
public class QuickBootFragment extends Fragment {

    public QuickBootFragment newInstance() {
        QuickBootFragment fragment = new QuickBootFragment();
        return fragment;
    }

    public QuickBootFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_quick_boot, container, false);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        final ArrayList<BootObjects> list = new ArrayList<BootObjects>();
        list.add(new BootObjects("Shutdown", R.mipmap.ic_shutdown));
        list.add(new BootObjects("Reboot", R.mipmap.ic_reboot));
        list.add(new BootObjects("Reboot Recovery", R.mipmap.ic_reboot_recovery));
        list.add(new BootObjects("Reboot Bootloader", R.mipmap.ic_reboot_bootloader));
        list.add(new BootObjects("Hot Reboot", R.mipmap.ic_hot_reboot));
        ListView listView = (ListView) rootView.findViewById(R.id.bootMenuListView);
        QuickBootMenuAdapter adapter = new QuickBootMenuAdapter(rootView.getContext(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedAction = list.get(position).getBootText();
                Boolean confirmReboot = prefs.getBoolean("confirm_action", true);
                performSelectedAction(rootView.getContext(), selectedAction, confirmReboot);
                Log.e("Boot menu item clicked!", selectedAction + " " + confirmReboot);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void performSelectedAction(final Context context, final String selectedAction, Boolean confirmReboot){
        if (confirmReboot){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Confirm");
            dialog.setMessage("Are you sure you want to " + selectedAction + "?");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*switch (selectedAction) {
                        case "Shutdown":
                            Rebooter.shutdown(context);
                            break;
                        case "Reboot":
                            Rebooter.reboot(context, "");
                            break;
                        case "Reboot Recovery":
                            Rebooter.reboot(context, " Recovery");
                            break;
                        case "Reboot Bootloader":
                            Rebooter.reboot(context, " Bootloader");
                            break;
                        case "Hot Reboot":
                            Rebooter.hotReboot(context);
                            break;
                    }*/
                    Rebooter.rebootController(context, selectedAction);
                    getActivity().finish();
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            switch (selectedAction) {
                case "Shutdown":
                    Rebooter.shutdown(context);
                    break;
                case "Reboot":
                    Rebooter.reboot(context, "");
                    break;
                case "Reboot Recovery":
                    Rebooter.reboot(context, " Recovery");
                    break;
                case "Reboot Bootloader":
                    Rebooter.reboot(context, " Bootloader");
                    break;
                case "Hot Reboot":
                    Rebooter.hotReboot(context);
                    break;
            }
        }
    }

    private static class QuickBootMenuAdapter extends BaseAdapter {

        private ArrayList<BootObjects> list;
        private Context context;
        private LayoutInflater layoutInflater;

        public QuickBootMenuAdapter(Context c, ArrayList<BootObjects> l){
            this.list = l;
            this.context = c;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.boot_menu_row, null);
                holder = new ViewHolder();
                holder.bootIcon = (ImageView) convertView.findViewById(R.id.bootMenuRowIcon);
                holder.bootText = (TextView) convertView.findViewById(R.id.bootMenuRowText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bootIcon.setImageResource(list.get(position).getBootId());
            holder.bootText.setText(list.get(position).getBootText());
            return convertView;
        }

        class ViewHolder{
            ImageView bootIcon;
            TextView bootText;
        }
    }
}