package com.ros.smartrocket.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

public class CashingOutSuccessActivity extends BaseActivity implements View.OnClickListener {
    public CashingOutSuccessActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashing_out_success);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                finish();
                startActivity(IntentUtils.getMainActivityIntent(this));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_title);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(IntentUtils.getMainActivityIntent(this));
    }
}