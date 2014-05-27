package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.PreferencesManager;

public class TermsAndConditionActivity extends BaseActivity {
    //private static final String TAG = TermsAndConditionActivity.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_terms_and_condition);

        int versionId = 1;
        if (getIntent() != null) {
            versionId = getIntent().getIntExtra(Keys.T_AND_C_VERSION, 1);
        }

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(String.format(Config.TERMS_AND_CONDITION_URL, preferencesManager.getLanguageCode(),
                String.valueOf(versionId)));
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
