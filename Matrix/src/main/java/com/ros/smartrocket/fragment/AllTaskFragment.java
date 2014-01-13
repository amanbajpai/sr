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
    private FragmentHelper fragmetHelper = new FragmentHelper();
    private String contentType = Keys.FIND_TASK;

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

        Log.i(TAG, "onCreateView() [contentType  =  " + contentType + "]");
        showMap();
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
            showMap();
        } else {
            fragmetHelper.hideLastFragment(getActivity());
        }
    }

    /**
     * Show Map with proper mode
     */
    public void showMap() {
        Keys.MapViewMode mode = Keys.MapViewMode.ALLTASKS;
        if (Keys.FIND_TASK.equals(contentType)) {
            mode = Keys.MapViewMode.ALLTASKS;
        } else if (Keys.MY_TASK.equals(contentType)) {
            mode = Keys.MapViewMode.MYTASKS;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Keys.MAP_MODE_VIEWTYPE, mode.toString());

        Log.i(TAG, "onClick() [findTasksButton]");

        Fragment fragment = new TasksMapFragment();
        fragment.setArguments(bundle);
        fragmetHelper.startFragmentFromStack(getActivity(), fragment);
    }

    public void showSurveyList() {
        fragmetHelper.startFragmentFromStack(getActivity(), new SurveyListFragment());
    }

    public void showMyTask() {
        fragmetHelper.startFragmentFromStack(getActivity(), new MyTaskListFragment());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mapButton:
                showMap();
                break;
            case R.id.listButton:
                if (Keys.FIND_TASK.equals(contentType)) {
                    showSurveyList();
                } else if (Keys.MY_TASK.equals(contentType)) {
                    showMyTask();
                }
                break;
            case R.id.refreshButton:

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
        view.findViewById(R.id.mapButton).setOnClickListener(this);
        view.findViewById(R.id.listButton).setOnClickListener(this);
        view.findViewById(R.id.refreshButton).setOnClickListener(this);

        if (Keys.FIND_TASK.equals(contentType)) {
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.find_task_title);
        } else if (Keys.MY_TASK.equals(contentType)) {
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.my_tasks_title);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
}
