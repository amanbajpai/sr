package com.ros.smartrocket.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
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
import android.widget.TextView;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.flow.map.TasksMapFragment;
import com.ros.smartrocket.ui.adapter.WaveAdapter;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.flow.base.BaseFragment;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.interfaces.DistancesUpdateListener;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

public class WaveListFragment extends BaseFragment implements OnItemClickListener, NetworkOperationListenerInterface,
        View.OnClickListener {
    private static final String TAG = WaveListFragment.class.getSimpleName();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ImageView refreshButton;
    private AsyncQueryHandler handler;
    private WaveAdapter adapter;
    private TextView emptyListLTextView;
    private boolean isFirstStart = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_wave_list, null);

        initActionBarView();
        refreshIconState(true);

        handler = new DbHandler(getActivity().getContentResolver());
        adapter = new WaveAdapter(getActivity());

        emptyListLTextView = (TextView) view.findViewById(R.id.emptyListLTextView);
        emptyListLTextView.setText(R.string.loading_missions);

        ListView waveList = (ListView) view.findViewById(R.id.waveList);
        waveList.setEmptyView(emptyListLTextView);
        waveList.setOnItemClickListener(this);
        waveList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isHidden()) {
            getWaves(true);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getWaves(false);
        }
    }

    private void getWaves(final boolean updateFromServer) {
        if (preferencesManager.getUseLocationServices() && lm.isConnected()) {
            AllTaskFragment.stopRefreshProgress = !updateFromServer;
            refreshIconState(true);

            App.getInstance().getLocationManager().recalculateDistances(new DistancesUpdateListener() {
                @Override
                public void onDistancesUpdated() {
                    WavesBL.getNotMyTasksWavesListFromDB(handler, TasksMapFragment.taskRadius,
                            preferencesManager.getShowHiddenTask());

                    if (updateFromServer) {
                        updateDataFromServer();
                    }
                }
            });

        } else {
            refreshIconState(false);
            adapter.setData(new ArrayList<Wave>());
        }
    }

    /**
     * Send request to server for data update
     */
    private void updateDataFromServer() {
        final int radius = TasksMapFragment.taskRadius;
        if (UIUtils.isOnline(getActivity())) {
            MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
                @Override
                public void getLocationStart() {

                }

                @Override
                public void getLocationInProcess() {

                }

                @Override
                public void getLocationSuccess(final Location location) {
                    if (isFirstStart) {
                        isFirstStart = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                APIFacade.getInstance().getWaves(getActivity(), location.getLatitude(), location.getLongitude(), radius);
                            }
                        }, 1000);
                    } else {
                        APIFacade.getInstance().getWaves(getActivity(), location.getLatitude(), location.getLongitude(), radius);
                    }
                }

                @Override
                public void getLocationFail(String errorText) {
                    UIUtils.showSimpleToast(App.getInstance(), errorText);
                }
            });
        } else {
            refreshIconState(false);
            UIUtils.showSimpleToast(getActivity(), R.string.no_internet);
        }
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case WaveDbSchema.QueryWaveByDistance.TOKEN_QUERY:
                    adapter.setData(WavesBL.convertCursorToWaveListByDistance(cursor));
                    if (AllTaskFragment.stopRefreshProgress) {
                        if (adapter.getCount() == 0) {
                            emptyListLTextView.setText(R.string.no_mission_available);
                        }
                        refreshIconState(false);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.GET_WAVES_OPERATION_TAG.equals(operation.getTag())) {
            AllTaskFragment.stopRefreshProgress = true;
            WavesBL.getNotMyTasksWavesListFromDB(handler, TasksMapFragment.taskRadius,
                    preferencesManager.getShowHiddenTask());
        }
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.GET_WAVES_OPERATION_TAG.equals(operation.getTag())) {
            if (operation.getResponseStatusCode() == BaseNetworkService.DEVICE_INTEERNAL_ERROR) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                L.e(TAG, operation.getResponseError());
                refreshIconState(false);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Wave wave = adapter.getItem(position);
        startActivity(IntentUtils.getWaveDetailsIntent(getActivity(), wave.getId(), 0, WavesBL.isPreClaimWave(wave)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshButton:
                getWaves(true);
                IntentUtils.refreshProfileAndMainMenu(getActivity());
                IntentUtils.refreshMainMenuMyTaskCount(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        initActionBarView();

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initActionBarView() {
        if (refreshButton == null) {
            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
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
        if (refreshButton != null) {
            refreshButton.setOnClickListener(this);
        }
    }

    private void refreshIconState(boolean isLoading) {
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
