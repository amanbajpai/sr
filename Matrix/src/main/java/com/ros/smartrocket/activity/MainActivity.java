package com.ros.smartrocket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.fragment.AllTaskFragment;
import com.ros.smartrocket.helpers.FragmentHelper;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.UIUtils;

public class MainActivity extends BaseSlidingMenuActivity {
    //private static final String TAG = MainActivity.class.getSimpleName();
    private FragmentHelper fragmetHelper = new FragmentHelper();
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmetHelper.removeFragmentFromList(this, new AllTaskFragment());

        Bundle bundle = new Bundle();
        bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);

        Fragment fragment = new AllTaskFragment();
        fragment.setArguments(bundle);
        fragmetHelper.startFragmentFromStack(this, fragment);

        startService(new Intent(this, UploadFileService.class).setAction(Keys.ACTION_CHECK_NOT_UPLOADED_FILES));

        /*Intent intent = new Intent(this, MainActivity.class);
        NotificationUtils.generateNotification(this, "Title", "Message text\n, Message text, <b>Message text,</b> " +
                "Message text, Message text, Message text, Message text, Message text, Message text, Message text, " +
                "Message text, Message text, ", intent);*/


        //NotificationUtils.showOverlayNotification(App.getInstance(), "Test text", "Description. Description", null);
    }

    public void startFragment(Fragment fragment) {
        fragmetHelper.startFragmentFromStack(this, fragment);
    }

    public void removeFragmentFromList(Fragment fragment) {
        fragmetHelper.removeFragmentFromList(this, fragment);
    }

    public void startActivity(Activity activity) {
        Intent i = new Intent(this, activity.getClass());
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        UIUtils.showSimpleToast(this, getString(R.string.click_back_again_to_exit));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
