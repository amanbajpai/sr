package com.ros.smartrocket.flow.share;

import android.os.Bundle;
import android.view.MenuItem;

import com.ros.smartrocket.flow.base.BaseActivity;

public class ShareActivity extends BaseActivity {

    public ShareActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUp();
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
