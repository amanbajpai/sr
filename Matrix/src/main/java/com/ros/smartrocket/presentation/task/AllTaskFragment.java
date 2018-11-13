package com.ros.smartrocket.presentation.task;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.presentation.task.map.TasksMapFragment;
import com.ros.smartrocket.presentation.task.my.MyTaskListFragment;
import com.ros.smartrocket.presentation.wave.my.WaveListFragment;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.helpers.FragmentHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AllTaskFragment extends BaseFragment implements OnClickListener {
    @BindView(R.id.mapButton)
    LinearLayout mapButton;
    @BindView(R.id.listButton)
    LinearLayout listButton;
    @BindView(R.id.tabsLayout)
    LinearLayout tabsLayout;
    Unbinder unbinder;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private FragmentHelper fragmentHelper = new FragmentHelper();
    private String contentType = Keys.FIND_TASK;
    public static boolean stopRefreshProgress;
    private PushReceiver localReceiver;

    public AllTaskFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_all_task, null);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) contentType = getArguments().getString(Keys.CONTENT_TYPE);
        mapButton.setOnClickListener(this);
        listButton.setOnClickListener(this);
        showDefaultFragment();
        localReceiver = new PushReceiver();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (getArguments() != null) contentType = getArguments().getString(Keys.CONTENT_TYPE);
            showDefaultFragment();
        } else {
            fragmentHelper.hideLastFragment(getActivity());
        }
    }

    public void showDefaultFragment() {
        if (Keys.FIND_TASK.equals(contentType)) {
            if (preferencesManager.getIsFirstLogin()) {
                preferencesManager.setIsFirstLogin(false);
                showList();
            } else {
                showMap();
            }
        } else if (Keys.MY_TASK.equals(contentType)) {
            showList();
        }
    }

    public void showMap() {
        tabsLayout.setBackgroundResource(R.drawable.tabs_map_bg);
        mapButton.setSelected(true);
        listButton.setSelected(false);

        Keys.MapViewMode mode = Keys.MapViewMode.ALL_TASKS;
        if (Keys.FIND_TASK.equals(contentType))
            mode = Keys.MapViewMode.ALL_TASKS;
        else if (Keys.MY_TASK.equals(contentType))
            mode = Keys.MapViewMode.MY_TASKS;
        Bundle bundle = new Bundle();
        bundle.putString(Keys.MAP_MODE_VIEWTYPE, mode.toString());
        Fragment fragment = new TasksMapFragment();
        fragment.setArguments(bundle);
        fragmentHelper.startFragmentFromStack(getActivity(), fragment, R.id.map_list_content_frame);
    }

    public void showList() {
        tabsLayout.setBackgroundResource(R.drawable.tabs_list_bg);
        mapButton.setSelected(false);
        listButton.setSelected(true);
        if (Keys.FIND_TASK.equals(contentType))
            fragmentHelper.startFragmentFromStack(getActivity(), new WaveListFragment(), R.id.map_list_content_frame);
        else if (Keys.MY_TASK.equals(contentType))
            fragmentHelper.startFragmentFromStack(getActivity(), new MyTaskListFragment(), R.id.map_list_content_frame);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mapButton:
                showMap();
                break;
            case R.id.listButton:
                showList();
                break;
            case R.id.starButton:
                startActivity(IntentUtils.getNotificationsIntent(getActivity()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            View view = actionBar.getCustomView();
            if (view == null) actionBar.setCustomView(R.layout.actionbar_custom_view_all_task);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            view = actionBar.getCustomView();
            if (Keys.FIND_TASK.equals(contentType))
                ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.find_mission);
            else if (Keys.MY_TASK.equals(contentType))
                ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.my_missions);
            if (PreferencesManager.getInstance().showPushNotifStar()) {
                view.findViewById(R.id.starButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.starButton).setOnClickListener(this);
            } else {
                view.findViewById(R.id.starButton).setVisibility(View.GONE);
                view.findViewById(R.id.starButton).setOnClickListener(null);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.REFRESH_PUSH_NOTIFICATION_LIST);
        getActivity().registerReceiver(localReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(localReceiver);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class PushReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().supportInvalidateOptionsMenu();
        }
    }


}
