package com.ros.smartrocket.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.fragment.AllTaskFragment;
import com.ros.smartrocket.helpers.FragmentHelper;
import com.ros.smartrocket.net.TaskReminderService;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.net.gcm.CommonUtilities;
import com.ros.smartrocket.utils.UIUtils;

public class MainActivity extends BaseSlidingMenuActivity {
    private FragmentHelper fragmentHelper = new FragmentHelper();
    private boolean doubleBackToExitPressedOnce = false;
    private static final int DOUBLE_PRESS_INTERVAL_MILLISECONDS = 2000;
    private ResponseReceiver localReceiver;

    public MainActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentHelper.removeFragmentFromList(this, new AllTaskFragment());

        Bundle bundle = new Bundle();
        bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);

        Fragment fragment = new AllTaskFragment();
        fragment.setArguments(bundle);
        fragmentHelper.startFragmentFromStack(this, fragment);

        startService(new Intent(this, UploadFileService.class).setAction(Keys.ACTION_CHECK_NOT_UPLOADED_FILES));
        startService(new Intent(this, TaskReminderService.class).setAction(Keys.ACTION_START_REMINDER_TIMER));

        CommonUtilities.registerGCMInBackground();

        localReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.FINISH_MAIN_ACTIVITY);

        registerReceiver(localReceiver, intentFilter);

        /*NotificationUtils.startExpiredNotificationActivity(this, "missionName", "locationName", "missionAddress");*/
        /*NotificationUtils.startApprovedNotificationActivity(this, "Validation Text", "missionName", "locationName",
                "missionAddress");*/
        /*NotificationUtils.startRedoNotificationActivity(this, 18, 100, "missionName", "locationName", "missionAddress");*/
       /* NotificationUtils.startRejectNotificationActivity(this, "Validation Text", "missionName", "locationName",
                "missionAddress");*/

        /*long deadlineTime = Calendar.getInstance().getTimeInMillis();
        NotificationUtils.startDeadlineNotificationActivity(this, deadlineTime, 18, 100, "missionName", "locationName",
                "missionAddress");*/
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Keys.FINISH_MAIN_ACTIVITY)) {
                finish();
            }
        }
    }

    public void startFragment(Fragment fragment) {
        fragmentHelper.startFragmentFromStack(this, fragment);
    }

    public void removeFragmentFromList(Fragment fragment) {
        fragmentHelper.removeFragmentFromList(this, fragment);
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
        }, DOUBLE_PRESS_INTERVAL_MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(localReceiver);
        super.onDestroy();
    }
}
