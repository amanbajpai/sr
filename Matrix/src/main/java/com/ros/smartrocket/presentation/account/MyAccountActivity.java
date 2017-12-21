package com.ros.smartrocket.presentation.account;

import android.os.Bundle;
import android.view.MenuItem;

import com.ros.smartrocket.presentation.base.BaseActivity;

public class MyAccountActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUp();
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new MyAccountFragment()).commit();
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
