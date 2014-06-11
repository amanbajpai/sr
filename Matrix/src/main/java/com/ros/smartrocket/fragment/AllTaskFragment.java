package com.ros.smartrocket.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.helpers.FragmentHelper;

/**
 * Share app info fragment
 */
public class AllTaskFragment extends Fragment implements OnClickListener {
    private static final String TAG = AllTaskFragment.class.getSimpleName();
    private ViewGroup view;
    private FragmentHelper fragmentHelper = new FragmentHelper();
    private String contentType = Keys.FIND_TASK;
    private LinearLayout tabsLayout;
    private LinearLayout mapButton;
    private LinearLayout listButton;

    public AllTaskFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_all_task, null);

        if (getArguments() != null) {
            contentType = getArguments().getString(Keys.CONTENT_TYPE);
        }

        tabsLayout = (LinearLayout) view.findViewById(R.id.tabsLayout);

        mapButton = (LinearLayout) view.findViewById(R.id.mapButton);
        mapButton.setOnClickListener(this);

        listButton = (LinearLayout) view.findViewById(R.id.listButton);
        listButton.setOnClickListener(this);

        Log.i(TAG, "onCreateView() [contentType  =  " + contentType + "]");

        showDefaultFragment();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.i(TAG, "onHiddenChanged() [hidden  =  " + hidden + "]");

        if (!hidden) {
            if (getArguments() != null) {
                contentType = getArguments().getString(Keys.CONTENT_TYPE);
                Log.i(TAG, "onHiddenChanged() [contentType  =  " + contentType + "]");
            }
            showDefaultFragment();
        } else {
            fragmentHelper.hideLastFragment(getActivity());
        }
    }

    public void showDefaultFragment() {
        if (Keys.FIND_TASK.equals(contentType)) {
            showMap();
        } else if (Keys.MY_TASK.equals(contentType)) {
            showList();
        }
    }

    /**
     * Show Map with proper mode
     */
    public void showMap() {
        tabsLayout.setBackgroundResource(R.drawable.tabs_map_bg);
        mapButton.setSelected(true);
        listButton.setSelected(false);

        Keys.MapViewMode mode = Keys.MapViewMode.ALL_TASKS;
        if (Keys.FIND_TASK.equals(contentType)) {
            mode = Keys.MapViewMode.ALL_TASKS;
        } else if (Keys.MY_TASK.equals(contentType)) {
            mode = Keys.MapViewMode.MY_TASKS;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Keys.MAP_MODE_VIEWTYPE, mode.toString());

        Fragment fragment = new TasksMapFragment();
        fragment.setArguments(bundle);
        fragmentHelper.startFragmentFromStack(getActivity(), fragment, R.id.map_list_content_frame);
    }

    /**
     * Show List with proper contentType
     */
    public void showList() {
        tabsLayout.setBackgroundResource(R.drawable.tabs_list_bg);
        mapButton.setSelected(false);
        listButton.setSelected(true);

        if (Keys.FIND_TASK.equals(contentType)) {
            fragmentHelper.startFragmentFromStack(getActivity(), new WaveListFragment(),
                    R.id.map_list_content_frame);
        } else if (Keys.MY_TASK.equals(contentType)) {
            fragmentHelper.startFragmentFromStack(getActivity(), new MyTaskListFragment(),
                    R.id.map_list_content_frame);
        }
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
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_all_task);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();

        if (Keys.FIND_TASK.equals(contentType)) {
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.find_mission);
        } else if (Keys.MY_TASK.equals(contentType)) {
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.my_missions);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
}
