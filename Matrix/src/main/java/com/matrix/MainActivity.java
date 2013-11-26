package com.matrix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.matrix.activity.BaseSlidingMenuActivity;
import com.matrix.fragment.SurveyListFragment;
import com.matrix.fragment.TasksMapFragment;

import java.util.ArrayList;

public class MainActivity extends BaseSlidingMenuActivity {
    //private final static String TAG = MainActivity.class.getSimpleName();

    private ArrayList<Fragment> mFragmentList;
    private Fragment lastFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }

        startFragment(new TasksMapFragment());
    }

    public void startFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (lastFragment != null) ft.hide(lastFragment);

        boolean containFragment = false;
        for (int i = 0; i < mFragmentList.size(); i++) {
            if (mFragmentList.get(i).getClass().equals(fragment.getClass())) {
                containFragment = true;
                break;
            }
        }

        if (containFragment) {
            for (int i = 0; i < mFragmentList.size(); i++) {
                if (mFragmentList.get(i).getClass().equals(fragment.getClass())) {
                    lastFragment = mFragmentList.get(i);
                    break;
                }
            }

            ft.show(lastFragment);
        } else {

            lastFragment = fragment;
            mFragmentList.add(lastFragment);
            ft.add(R.id.content_frame, lastFragment, fragment.getClass().toString());
        }
        ft.commitAllowingStateLoss();

    }

    public void startActivity(Activity activity) {
        Intent i = new Intent(this, activity.getClass());
        startActivity(i);
    }

    public void removeFragmentFromList(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        try {
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int fragmentIdToRemove = -1;
        for (int i = 0; i < mFragmentList.size(); i++) {
            if (mFragmentList.get(i).getClass().equals(fragment.getClass())) {
                fragmentIdToRemove = i;
                break;
            }
        }

        if (fragmentIdToRemove != -1) mFragmentList.remove(fragmentIdToRemove);
    }

    public void removeAllFragmentFromList() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : mFragmentList) {
            ft.remove(fragment);
        }
        try {
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mFragmentList.clear();
    }

}
