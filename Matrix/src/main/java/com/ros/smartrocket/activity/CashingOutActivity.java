package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.ros.smartrocket.fragment.CashingOutFragment;

/**
 * Activity for view Settings
 */
public class CashingOutActivity extends BaseActivity {

    public CashingOutActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new CashingOutFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

}
