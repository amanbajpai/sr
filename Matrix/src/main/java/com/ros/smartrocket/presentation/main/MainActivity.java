package com.ros.smartrocket.presentation.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.net.TaskReminderService;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.net.gcm.CommonUtilities;
import com.ros.smartrocket.presentation.task.AllTaskFragment;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.eventbus.LogOutAction;
import com.ros.smartrocket.utils.helpers.FragmentHelper;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseSlidingMenuActivity {
    private FragmentHelper fragmentHelper = new FragmentHelper();
    private boolean doubleBackToExitPressedOnce = false;
    private static final int DOUBLE_PRESS_INTERVAL_MILLISECONDS = 2000;
    private ResponseReceiver localReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStartFragment();
        startServices();
        initPushMessaging();
        initReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.CHINESE)
            JPushInterface.onResume(this);
    }

    private void initReceiver() {
        localReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.FINISH_MAIN_ACTIVITY);
        registerReceiver(localReceiver, intentFilter);
    }

    private void initStartFragment() {
        fragmentHelper.removeFragmentFromList(this, new AllTaskFragment());
        Bundle bundle = new Bundle();
        bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);
        Fragment fragment = new AllTaskFragment();
        fragment.setArguments(bundle);
        fragmentHelper.startFragmentFromStack(this, fragment);
    }

    private void startServices() {
        startService(new Intent(this, UploadFileService.class).setAction(Keys.ACTION_CHECK_NOT_UPLOADED_FILES));
        startService(new Intent(this, TaskReminderService.class).setAction(Keys.ACTION_START_REMINDER_TIMER));
    }

    private void initPushMessaging() {
        if (!Config.USE_BAIDU)
            CommonUtilities.submitFCMToAppServer();
        else
            JPushInterface.init(getApplicationContext());
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Keys.FINISH_MAIN_ACTIVITY) && !isDestroyed())
                finish();
        }
    }

    public void startFragment(Fragment fragment) {
        fragmentHelper.startFragmentFromStack(this, fragment);
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
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, DOUBLE_PRESS_INTERVAL_MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(localReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.CHINESE)
            JPushInterface.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(LogOutAction event) {
        if (!isDestroyed()) finish();
    }


}
