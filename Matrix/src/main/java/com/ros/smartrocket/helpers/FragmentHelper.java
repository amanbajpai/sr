package com.ros.smartrocket.helpers;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.ros.smartrocket.R;

import java.util.ArrayList;

/**
 * Singleton class for work with server API
 */
public class FragmentHelper {
    private static final String TAG = FragmentHelper.class.getSimpleName();
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private Fragment lastFragment;

    public FragmentHelper() {
    }

    /**
     * @param activity
     * @param fragment
     */
    public void startFragmentFromStack(Activity activity, Fragment fragment) {
        Log.i(TAG, "startFragmentFromStack() [" + fragment + "]");
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();

        if (lastFragment != null) {
            ft.hide(lastFragment);
        }

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
                    if(fragment.getArguments() != null){
                        lastFragment.getArguments().putAll(fragment.getArguments());
                    }
                    break;
                }
            }

            ft.show(lastFragment);
        } else {
            lastFragment = fragment;
            fragmentList.add(lastFragment);
            ft.add(R.id.content_frame, lastFragment, fragment.getClass().toString());
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * @param activity
     * @param fragment
     */
    public void removeFragmentFromList(Activity activity, Fragment fragment) {
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        try {
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * @param activity
     */
    public void removeAllFragmentFromList(Activity activity) {
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragmentList) {
            ft.remove(fragment);
        }
        try {
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentList.clear();
    }

    public void hideLastFragment(Activity activity){
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        if (lastFragment != null) {
            ft.hide(lastFragment);
        }
        ft.commitAllowingStateLoss();
    }

    public void showLastFragment(Activity activity){
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        if (lastFragment != null) {
            ft.show(lastFragment);
        }
        ft.commitAllowingStateLoss();
    }
}
