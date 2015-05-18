package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
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

        String termsUrl;
        switch (preferencesManager.getLanguageCode()) {
            case "en_SG":
            case "zh_CN":
                termsUrl = "http://smart-rocket.com/zh-hans/terms/";
                break;
            case "zh":
            case "zh_TW":
            case "zh_HK":
                termsUrl = "http://smart-rocket.com/zh-hant/terms-of-service-cnt/";
                break;
            default:
                termsUrl = "http://smart-rocket.com/terms-of-service/";
                break;
        }

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(termsUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.term_and_conditions_title);

        return true;
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
