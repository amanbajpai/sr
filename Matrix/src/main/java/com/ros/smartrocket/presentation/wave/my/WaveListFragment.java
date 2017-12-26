package com.ros.smartrocket.presentation.wave.my;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.task.Wave;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.map.CurrentLocatiuonListener;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.presentation.task.AllTaskFragment;
import com.ros.smartrocket.presentation.task.map.TasksMapFragment;
import com.ros.smartrocket.ui.adapter.WaveAdapter;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WaveListFragment extends BaseFragment implements OnItemClickListener, MyWaveMvpView {
    public static final int DELAY_MILLIS = 1000;
    @BindView(R.id.emptyListLTextView)
    CustomTextView emptyListTextView;
    @BindView(R.id.waveList)
    ListView waveList;
    Unbinder unbinder;
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private ImageView refreshButton;
    private WaveAdapter adapter;
    private boolean isFirstStart = true;
    private MyWaveMvpPresenter<MyWaveMvpView> presenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_wave_list, null);
        unbinder = ButterKnife.bind(this, view);
        presenter = new MyWavePresenter<>();
        initUI();
        return view;
    }

    private void initUI() {
        initActionBarView();
        adapter = new WaveAdapter(getActivity());
        emptyListTextView.setText(R.string.loading_missions);
        waveList.setEmptyView(emptyListTextView);
        waveList.setOnItemClickListener(this);
        waveList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.attachView(this);
        if (!isHidden()) getWaves(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.detachView();
        refreshIconState(false);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initActionBarView();
            getWaves(false);
        }
    }

    private void getWaves(final boolean updateFromServer) {
        if (preferencesManager.getUseLocationServices() && lm.isConnected()) {
            AllTaskFragment.stopRefreshProgress = !updateFromServer;
            refreshIconState(true);
            App.getInstance().getLocationManager().recalculateDistances(() -> {
                presenter.loadNotMyWavesListFromDB(preferencesManager.getShowHiddenTask());
                if (updateFromServer) updateDataFromServer();
            });
        } else {
            refreshIconState(false);
            adapter.setData(new ArrayList<>());
        }
    }

    private void updateDataFromServer() {
        final int radius = TasksMapFragment.taskRadius;
        if (UIUtils.isOnline(getActivity())) {
            MatrixLocationManager.getCurrentLocation(false, new CurrentLocatiuonListener() {
                @Override
                public void getLocationSuccess(Location location) {
                    if (isFirstStart)
                        new Handler().postDelayed(() -> presenter.getWavesFromServer(location.getLatitude(), location.getLongitude(), radius),
                                DELAY_MILLIS);
                    else
                        presenter.getWavesFromServer(location.getLatitude(), location.getLongitude(), radius);

                }

                @Override
                public void getLocationFail(String errorText) {
                    UIUtils.showSimpleToast(App.getInstance(), errorText);
                }
            });
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.no_internet);
        }
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        if (networkError.getErrorCode() == NetworkError.DEVICE_INTEERNAL_ERROR) {
            if (getActivity() != null) getActivity().finish();
        } else {
            UIUtils.showSimpleToast(getActivity(), networkError.getErrorMessageRes());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Wave wave = adapter.getItem(position);
        startActivity(IntentUtils.getWaveDetailsIntent(getActivity(), wave.getId(), 0, WavesBL.isPreClaimWave(wave)));
    }

    private void refreshData() {
        getWaves(true);
        IntentUtils.refreshProfileAndMainMenu(getActivity());
        IntentUtils.refreshMainMenuMyTaskCount(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        initActionBarView();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initActionBarView() {
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (refreshButton == null && actionBar != null) {
            View view = actionBar.getCustomView();
            if (view != null) {
                initRefreshButton(actionBar);
            } else {
                actionBar.setCustomView(R.layout.actionbar_custom_view_all_task);
                initRefreshButton(actionBar);
            }
        }
    }

    public void initRefreshButton(ActionBar actionBar) {
        View view = actionBar.getCustomView();
        refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
        if (refreshButton != null) refreshButton.setOnClickListener(v -> refreshData());
    }

    @Override
    public void refreshIconState(boolean isLoading) {
        if (refreshButton != null && getActivity() != null) {
            if (isLoading) {
                refreshButton.setClickable(false);
                refreshButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
            } else {
                refreshButton.setClickable(true);
                refreshButton.clearAnimation();
            }
        }
    }

    @Override
    public void onWavesLoaded() {
        AllTaskFragment.stopRefreshProgress = true;
        presenter.loadNotMyWavesListFromDB(preferencesManager.getShowHiddenTask());
    }

    @Override
    public void onWavesLoadingComplete(List<Wave> list) {
        isFirstStart = false;
        adapter.setData(list);
        if (AllTaskFragment.stopRefreshProgress) {
            if (adapter.getCount() == 0) emptyListTextView.setText(R.string.no_mission_available);
            refreshIconState(false);
        }
    }
}
