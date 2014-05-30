package com.ros.smartrocket.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.utils.IntentUtils;

import java.util.List;

public class EmailRedirectActivity extends Activity {
    private static final String TAG = EmailRedirectActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            Uri data = getIntent().getData();
            if (data != null) {
                String scheme = data.getScheme(); // "http"
                String host = data.getHost(); // "twitter.com"
                List<String> params = data.getPathSegments();

                String email = params.get(1);
                String token = params.get(2);

                if (Keys.ACTIVATE_ACCOUNT.equals(params.get(0))) {
                    startActivity(IntentUtils.getActivateAccountIntent(this, email, token));
                } else if (Keys.FORGOT_PASS.equals(params.get(0))) {
                    startActivity(IntentUtils.getSetNewPasswordIntent(this, email, token));
                }
            }
        }

        finish();
    }
}
