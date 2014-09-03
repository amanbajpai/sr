package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.MenuItem;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new CashingOutFragment()).commit();
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
