package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.UIUtils;

public class EnterGroupCodeActivity extends BaseActivity implements View.OnClickListener {
    public final static String TAG = EnterGroupCodeActivity.class.getSimpleName();
    private EditText groupCodeEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_enter_group_code);

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        groupCodeEditText = (EditText) findViewById(R.id.groupCodeEditText);

        findViewById(R.id.continueButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                String groupCode = groupCodeEditText.getText().toString().trim();

                Intent intent = new Intent(this, CheckLocationActivity.class);
                intent.putExtra(Keys.GROUP_CODE, groupCode);
                startActivity(intent);
                break;
            default:
                break;
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
        return super.onOptionsItemSelected(item);
    }
}
