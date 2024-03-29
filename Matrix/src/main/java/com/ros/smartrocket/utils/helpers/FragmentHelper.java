package com.ros.smartrocket.utils.helpers;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;

import java.util.ArrayList;
import java.util.List;

public class FragmentHelper {
    private static final String TAG = FragmentHelper.class.getSimpleName();
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private Fragment lastFragment;

    public FragmentHelper() {
    }

    public void startFragmentFromStack(Activity activity, Fragment fragment) {
        startFragmentFromStack(activity, fragment, R.id.content_frame);
    }

    public void startFragmentFromStack(Activity activity, Fragment fragment, int layoutId) {
        Log.i(TAG, "startFragmentFromStack() [" + fragment + "]");
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        if (lastFragment != null) ft.hide(lastFragment);
        boolean containFragment = false;
        for (int i = 0; i < fragmentList.size(); i++) {
            if (fragmentList.get(i).getClass().equals(fragment.getClass())) {
                containFragment = true;
                break;
            }
        }

        if (containFragment) {
            for (int i = 0; i < fragmentList.size(); i++) {
                if (fragmentList.get(i).getClass().equals(fragment.getClass())) {
                    lastFragment = fragmentList.get(i);
                    if (fragment.getArguments() != null)
                        lastFragment.getArguments().putAll(fragment.getArguments());
                    break;
                }
            }
            ft.show(lastFragment);
        } else {
            lastFragment = fragment;
            fragmentList.add(lastFragment);
            ft.add(layoutId, lastFragment, fragment.getClass().toString());
        }
        ft.commitAllowingStateLoss();
    }

    public void removeFragmentFromList(Activity activity, Fragment fragment) {
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        try {
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            L.e(TAG, "RemoveFragmentFromList error" + e.getMessage(), e);
        }

        int fragmentIdToRemove = -1;
        for (int i = 0; i < fragmentList.size(); i++) {
            if (fragmentList.get(i).getClass().equals(fragment.getClass())) {
                fragmentIdToRemove = i;
                break;
            }
        }

        if (fragmentIdToRemove != -1) {
            fragmentList.remove(fragmentIdToRemove);
        }
    }

    public void hideLastFragment(Activity activity) {
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        if (lastFragment != null) {
            ft.hide(lastFragment);
        }
        ft.commitAllowingStateLoss();
    }
}
