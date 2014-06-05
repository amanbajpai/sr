package com.ros.smartrocket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.fragment.AllTaskFragment;
import com.ros.smartrocket.helpers.FragmentHelper;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.utils.UIUtils;

public class MainActivity extends BaseSlidingMenuActivity {
    private FragmentHelper fragmentHelper = new FragmentHelper();
    private boolean doubleBackToExitPressedOnce = false;
    private static final int DOUBLE_PRESS_INTERVAL_MILLISECONDS = 2000;

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
}
