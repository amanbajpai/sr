package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.ros.smartrocket.fragment.SettingsFragment;
import com.ros.smartrocket.fragment.ShareFragment;

/**
 * Activity for view Settings
 */
public class ShareActivity extends BaseActivity {

    public ShareActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new ShareFragment()).commit();
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
        return true;
    }

}
