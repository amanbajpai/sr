package com.matrix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.matrix.activity.BaseSlidingMenuActivity;
import com.matrix.fragment.AllTaskFragment;
import com.matrix.helpers.APIFacade;
import com.matrix.helpers.FragmentHelper;
import com.matrix.utils.PreferencesManager;

public class MainActivity extends BaseSlidingMenuActivity {
    //private final static String TAG = MainActivity.class.getSimpleName();
    private FragmentHelper fragmetHelper = new FragmentHelper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmetHelper.removeFragmentFromList(this, new AllTaskFragment());

        PreferencesManager pm = PreferencesManager.getInstance();
        if (pm.isGCMIdRegisteredOnServer()) {
            String regId = pm.getGCMRegistrationId();

            APIFacade.getInstance().testGCMPushNotification(getApplicationContext(), regId, "This is my test string");
        }

        Bundle bundle = new Bundle();
        bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);

        Fragment fragment = new AllTaskFragment();
        fragment.setArguments(bundle);
        fragmetHelper.startFragmentFromStack(this, fragment);

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
