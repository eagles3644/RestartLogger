package com.dugan.restartlogger;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

/**
 * Created by Todd on 4/25/2015.
 */
public class QuickBootMenu extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.quick_boot_menu);
        setFinishOnTouchOutside(true);
        QuickBootFragment quickBootFragment = (QuickBootFragment) getSupportFragmentManager().findFragmentById(R.id.quick_boot_fragment);
        quickBootFragment.newInstance();
    }
}
