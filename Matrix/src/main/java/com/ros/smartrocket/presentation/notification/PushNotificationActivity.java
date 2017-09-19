package com.ros.smartrocket.presentation.notification;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.ui.fragment.PushNotificationsListFragment;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;


public class PushNotificationActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TextUtils.isEmpty(PreferencesManager.getInstance().getToken())) {
            startActivity(IntentUtils.getLoginIntentForPushNotificationsActivity(this));
        }
        setHomeAsUp();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new
                    PushNotificationsListFragment()).commit();
        }

        PreferencesManager.getInstance().setShowPushNotifStar(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
