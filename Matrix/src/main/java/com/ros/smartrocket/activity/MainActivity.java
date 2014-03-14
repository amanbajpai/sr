package com.ros.smartrocket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.fragment.AllTaskFragment;
import com.ros.smartrocket.helpers.FragmentHelper;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.utils.UIUtils;

public class MainActivity extends BaseSlidingMenuActivity {
    //private static final String TAG = MainActivity.class.getSimpleName();
    private FragmentHelper fragmetHelper = new FragmentHelper();

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
}
