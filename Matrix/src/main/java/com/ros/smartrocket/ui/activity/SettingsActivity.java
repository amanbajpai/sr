package com.ros.smartrocket.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.ui.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    public SettingsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new SettingsFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

}
