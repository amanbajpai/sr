package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.PreferencesManager;

public class TermsAndConditionActivity extends BaseActivity {
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    public TermsAndConditionActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_terms_and_condition);

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
