package com.ros.smartrocket.flow.cash;

import android.os.Bundle;
import android.view.MenuItem;

import com.ros.smartrocket.flow.base.BaseActivity;

public class CashingOutActivity extends BaseActivity {

    public CashingOutActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUp();
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
