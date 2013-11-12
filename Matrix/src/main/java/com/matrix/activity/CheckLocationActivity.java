package com.matrix.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.BaseActivity;
import com.matrix.R;
import com.matrix.utils.UIUtils;

public class CheckLocationActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = CheckLocationActivity.class.getSimpleName();
    public EditText groupCodeEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_location);

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        groupCodeEditText = (EditText) findViewById(R.id.groupCodeEditText);

        findViewById(R.id.checkMyLocationButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkMyLocationButton:

                break;

        }
    }
}
