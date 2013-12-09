package com.matrix.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.helpers.FragmentHelper;

/**
 * Share app info fragment
 */
public class AllTaskFragment extends Fragment implements OnClickListener {
    //private static final String TAG = AllTaskFragment.class.getSimpleName();
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

        showMap();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (getArguments() != null) {
                contentType = getArguments().getString(Keys.CONTENT_TYPE);
            }
            showMap();
        } else {
            fragmetHelper.hideLastFragment(getActivity());
        }
    }

    public void showMap() {
        fragmetHelper.startFragmentFromStack(getActivity(), new TasksMapFragment());
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
