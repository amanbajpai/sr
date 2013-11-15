package com.matrix.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ToggleButton;
import com.matrix.BaseActivity;
import com.matrix.R;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.UIUtils;

public class SettingsFragment extends Fragment implements OnClickListener, NetworkOperationListenerInterface {
    //private static final String TAG = SettingsFragment.class.getSimpleName();
    private ViewGroup view;

    private Spinner languageSpinner;
    private SeekBar taskInRadiusSeekBar;

    private ToggleButton locationToggleButton;
    private ToggleButton pushMessagesToggleButton;
    private ToggleButton socialSharingToggleButton;
    private ToggleButton saveImageToggleButton;
    private ToggleButton tasksInLocationToggleButton;
    private ToggleButton fileSizeToggleButton;
    private ToggleButton deadlineReminderToggleButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_settings, null);

        languageSpinner = (Spinner) view.findViewById(R.id.languageSpinner);
        taskInRadiusSeekBar = (SeekBar) view.findViewById(R.id.taskInRadiusSeekBar);

        locationToggleButton = (ToggleButton) view.findViewById(R.id.locationToggleButton);
        pushMessagesToggleButton = (ToggleButton) view.findViewById(R.id.pushMessagesToggleButton);
        socialSharingToggleButton = (ToggleButton) view.findViewById(R.id.socialSharingToggleButton);
        saveImageToggleButton = (ToggleButton) view.findViewById(R.id.saveImageToggleButton);
        tasksInLocationToggleButton = (ToggleButton) view.findViewById(R.id.tasksInLocationToggleButton);
        fileSizeToggleButton = (ToggleButton) view.findViewById(R.id.fileSizeToggleButton);
        deadlineReminderToggleButton = (ToggleButton) view.findViewById(R.id.deadlineReminderToggleButton);


        view.findViewById(R.id.confirmAndSaveButton).setOnClickListener(this);
        view.findViewById(R.id.cancelButton).setOnClickListener(this);

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            //TODO Move to fragment second time
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            //TODO Process response
        } else {
            UIUtils.showSimpleToast(getActivity(), "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmAndSaveButton:

                break;
            case R.id.cancelButton:

                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().setTitle(R.string.app_settings_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((BaseActivity) getActivity()).addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        ((BaseActivity) getActivity()).removeNetworkOperationListener(this);
        super.onStop();
    }
}