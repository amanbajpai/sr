package com.ros.smartrocket.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.ui.fragment.ShareFragment;

/**
 * Activity for view Settings
 */
public class ShareActivity extends BaseActivity {

    public ShareActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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
