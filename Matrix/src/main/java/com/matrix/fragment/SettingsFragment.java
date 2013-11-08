package com.matrix.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

    public Spinner languageSpinner;
    public SeekBar taskInRadiusSeekBar;

    public ToggleButton locationToggleButton;
    public ToggleButton pushMessagesToggleButton;
    public ToggleButton socialSharingToggleButton;
    public ToggleButton saveImageToggleButton;
    public ToggleButton tasksInLocationToggleButton;
    public ToggleButton fileSizeToggleButton;
    public ToggleButton deadlineReminderToggleButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_settings, null);

        getActivity().setTitle(R.string.app_settings_title);

        languageSpinner= (Spinner) view.findViewById(R.id.languageSpinner);
        taskInRadiusSeekBar= (SeekBar) view.findViewById(R.id.taskInRadiusSeekBar);

        locationToggleButton= (ToggleButton) view.findViewById(R.id.locationToggleButton);
        pushMessagesToggleButton= (ToggleButton) view.findViewById(R.id.pushMessagesToggleButton);
        socialSharingToggleButton= (ToggleButton) view.findViewById(R.id.socialSharingToggleButton);
        saveImageToggleButton= (ToggleButton) view.findViewById(R.id.saveImageToggleButton);
        tasksInLocationToggleButton= (ToggleButton) view.findViewById(R.id.tasksInLocationToggleButton);
        fileSizeToggleButton= (ToggleButton) view.findViewById(R.id.fileSizeToggleButton);
        deadlineReminderToggleButton= (ToggleButton) view.findViewById(R.id.deadlineReminderToggleButton);


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