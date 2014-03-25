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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.adapter.SurveyAdapter;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.db.SurveyDbSchema;
import com.ros.smartrocket.db.entity.Survey;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

/**
 * Fragment - display all tasks in {@link android.widget.ListView}
 */
public class SurveyListFragment extends Fragment implements OnItemClickListener, NetworkOperationListenerInterface, View.OnClickListener {
    private static final String TAG = SurveyListFragment.class.getSimpleName();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ImageView refreshButton;
    private AsyncQueryHandler handler;
    private SurveyAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_survey_list, null);

        handler = new DbHandler(getActivity().getContentResolver());

        adapter = new SurveyAdapter(getActivity());

        ListView surveyList = (ListView) view.findViewById(R.id.surveyList);
        surveyList.setOnItemClickListener(this);
        surveyList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getSurveys();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            getSurveys();
        }
    }

    private void getSurveys() {
        refreshIconState(true);
        final Location location = lm.getLocation();
        if (location != null) {
            final int radius = TasksMapFragment.taskRadius;
            L.i(TAG, "Radius: " + radius);

            SurveysBL.getNotMyTasksSurveysListFromDB(handler, radius);

            lm.getAddress(location, new MatrixLocationManager.IAddress() {
                @Override
                public void onUpdate(Address address) {
                    if (address != null) {
                        apiFacade.getSurveys(getActivity(), location.getLatitude(), location.getLongitude(),
                                address.getCountryName(), address.getLocality(), radius);
                    } else {
                        UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined);
                    }
                }
            });
        }
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case SurveyDbSchema.QuerySurveyByDistance.TOKEN_QUERY:
                    ArrayList<Survey> surveys = SurveysBL.convertCursorToSurveyListByDistance(cursor);
                    adapter.setData(surveys);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_SURVEYS_OPERATION_TAG.equals(operation.getTag())) {
                SurveysBL.getNotMyTasksSurveysListFromDB(handler, TasksMapFragment.taskRadius);
            }
        } else {
            L.e(TAG, operation.getResponseError());
        }
        refreshIconState(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Survey survey = adapter.getItem(position);
        startActivity(IntentUtils.getSurveyDetailsIntent(getActivity(), survey));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshButton:
                getSurveys();
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
