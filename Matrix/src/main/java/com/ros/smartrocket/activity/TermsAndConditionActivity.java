package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.UIUtils;

public class TermsAndConditionActivity extends BaseActivity {
    private static final String TAG = TermsAndConditionActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_terms_and_condition);

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId="
                + UIUtils.getDeviceId(this), (long) 0).build());

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(Config.TERMS_AND_CONDITION_URL);
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
