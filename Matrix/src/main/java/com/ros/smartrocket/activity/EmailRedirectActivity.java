package com.ros.smartrocket.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

                if(params.size() > 2 && Keys.ACTIVATE_ACCOUNT.equals(params.get(2))){
                    String email = data.getQueryParameter("email");
                    String token = data.getQueryParameter("token");

                    startActivity(IntentUtils.getActivateAccountIntent(this, email, token));
                } else if(params.size() > 2 && Keys.FORGOT_PASS.equals(params.get(0))){
                    String email = params.get(1);
                    String token = params.get(2);

                    startActivity(IntentUtils.getSetNewPasswordIntent(this, email, token));
                }
            }
        }

        finish();
    }
}
