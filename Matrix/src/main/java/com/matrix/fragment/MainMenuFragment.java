package com.matrix.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.matrix.Keys;
import com.matrix.MainActivity;
import com.matrix.R;
import com.matrix.utils.L;

public class MainMenuFragment extends Fragment implements OnClickListener {
    private static final String TAG = MainMenuFragment.class.getSimpleName();
    private ViewGroup view;

    private ResponseReceiver localReceiver;
    private IntentFilter intentFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu, null);

        view.findViewById(R.id.findTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myAccountButton).setOnClickListener(this);
        view.findViewById(R.id.settingsButton).setOnClickListener(this);
        view.findViewById(R.id.aboutMatrixButton).setOnClickListener(this);
        view.findViewById(R.id.shareButton).setOnClickListener(this);

        localReceiver = new ResponseReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.REFRESH_MAIN_MENU);

        getActivity().registerReceiver(localReceiver, intentFilter);

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            //TODO Move to fragment second time
            L.i(TAG, "TODO Move to fragment second time");
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Keys.REFRESH_MAIN_MENU)) {
                //TODO Refresh main menu
                L.w(TAG, "TODO Refresh main menu");
            }
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Fragment fragment;

        switch (v.getId()) {
            case R.id.findTasksButton:
                bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);

                fragment = new AllTaskFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).startFragment(fragment);
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.myTasksButton:
                bundle.putString(Keys.CONTENT_TYPE, Keys.MY_TASK);

                fragment = new AllTaskFragment();
                fragment.setArguments(bundle);

                ((MainActivity) getActivity()).startFragment(fragment);
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.myAccountButton:
                ((MainActivity) getActivity()).startFragment(new MyAccountFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.settingsButton:
                ((MainActivity) getActivity()).startFragment(new SettingsFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.aboutMatrixButton:
                ((MainActivity) getActivity()).startFragment(new AboutMatrixFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.shareButton:
                ((MainActivity) getActivity()).startFragment(new ShareAndReferFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(localReceiver);
        }
    }
}
