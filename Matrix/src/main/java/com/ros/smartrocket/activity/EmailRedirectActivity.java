package com.ros.smartrocket.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.utils.IntentUtils;

import java.util.List;

public class EmailRedirectActivity extends Activity {

    public EmailRedirectActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            Uri data = getIntent().getData();
            if (data != null) {
                List<String> params = data.getPathSegments();

                if (params.size() > 2 && Keys.ACTIVATE_ACCOUNT.equals(params.get(2))) {
                    String email = data.getQueryParameter("email");
                    String token = data.getQueryParameter("token");

                    startActivity(IntentUtils.getActivateAccountIntent(this, email, token));
                } else if (params.size() > 1 && Keys.FORGOT_PASS.equals(params.get(1))) {
                    String email = data.getQueryParameter("email");
                    String token = data.getQueryParameter("token");

                    startActivity(IntentUtils.getSetNewPasswordIntent(this, email, token));
                }
            }
        }

        finish();
    }
}
