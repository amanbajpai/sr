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
import com.matrix.activity.LoginActivity;

public class MainMenuFragment extends Fragment implements OnClickListener {
    private static final String TAG = MainMenuFragment.class.getSimpleName();
    private ViewGroup view;

    private ResponseReceiver localReceiver;
    private IntentFilter intentFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu, null);

        view.findViewById(R.id.loginButton).setOnClickListener(this);
        view.findViewById(R.id.allTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myTasksButton).setOnClickListener(this);

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
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Keys.REFRESH_MAIN_MENU)) {
                //TODO Refresh main menu
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                ((MainActivity) getActivity()).startActivity(new LoginActivity());
                break;
            case R.id.allTasksButton:
                ((MainActivity) getActivity()).startFragment(new AllTaskListFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.myTasksButton:
                ((MainActivity) getActivity()).startFragment(new MylTaskListFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
        }
    }
}