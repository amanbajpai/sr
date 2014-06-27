package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.adapter.WaveAdapter;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

/**
 * Fragment - display all tasks in {@link android.widget.ListView}
 */
public class WaveListFragment extends Fragment implements OnItemClickListener, NetworkOperationListenerInterface,
        View.OnClickListener {
    private static final String TAG = WaveListFragment.class.getSimpleName();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ImageView refreshButton;
    private AsyncQueryHandler handler;
    private WaveAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_wave_list, null);

        handler = new DbHandler(getActivity().getContentResolver());
        adapter = new WaveAdapter(getActivity());

        TextView emptyListLTextView = (TextView) view.findViewById(R.id.emptyListLTextView);

        ListView waveList = (ListView) view.findViewById(R.id.waveList);
        waveList.setEmptyView(emptyListLTextView);
        waveList.setOnItemClickListener(this);
        waveList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getWaves();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            getWaves();
        }
    }

    private void getWaves() {
        refreshIconState(true);
        final Location location = lm.getLocation();
        if (location != null) {
            final int radius = TasksMapFragment.taskRadius;
            L.i(TAG, "Radius: " + radius);

            WavesBL.getNotMyTasksWavesListFromDB(handler, radius, preferencesManager.getShowHiddenTask());

            if (UIUtils.isOnline(getActivity()) && UIUtils.isGpsEnabled(getActivity())) {
                lm.getAddress(location, new MatrixLocationManager.IAddress() {
                    @Override
                    public void onUpdate(Address address) {
                        if (address != null) {
                            apiFacade.getWaves(getActivity(), location.getLatitude(), location.getLongitude(),
                                    address.getCountryName(), address.getLocality(), radius);
                        } else if (UIUtils.isOnline(getActivity())) {
                            UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined);
                        }
                    }
                });
            } else {
                refreshIconState(false);
                if (!UIUtils.isOnline(getActivity())) {
                    UIUtils.showSimpleToast(getActivity(), R.string.no_internet);
                }
            }
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
                    ArrayList<Wave> waves = WavesBL.convertCursorToWaveListByDistance(cursor);
                    adapter.setData(waves);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_WAVES_OPERATION_TAG.equals(operation.getTag())) {
                WavesBL.getNotMyTasksWavesListFromDB(handler, TasksMapFragment.taskRadius,
                        preferencesManager.getShowHiddenTask());
            }
        } else {
            L.e(TAG, operation.getResponseError());
        }
        refreshIconState(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Wave wave = adapter.getItem(position);
        startActivity(IntentUtils.getWaveDetailsIntent(getActivity(), wave));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshButton:
                getWaves();
                IntentUtils.refreshProfileAndMainMenu(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();

        View view = actionBar.getCustomView();
        refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshIconState(boolean isLoading) {
        if (refreshButton != null && getActivity() != null) {
            if (isLoading) {
                refreshButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
            } else {
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