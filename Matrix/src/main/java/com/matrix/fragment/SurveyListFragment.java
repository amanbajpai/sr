package com.matrix.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.matrix.App;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.activity.SurveysTaskListActivity;
import com.matrix.adapter.SurveyAdapter;
import com.matrix.db.SurveyDbSchema;
import com.matrix.db.entity.Survey;
import com.matrix.helpers.APIFacade;
import com.matrix.location.MatrixLocationManager;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.L;

import java.util.ArrayList;

/**
 * Fragment - display all tasks in {@link android.widget.ListView}
 */
public class SurveyListFragment extends Fragment implements OnItemClickListener, NetworkOperationListenerInterface {
    private static final String TAG = SurveyListFragment.class.getSimpleName();
    private static String DEFAULT_LANG = java.util.Locale.getDefault().getLanguage();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ViewGroup view;

    private AsyncQueryHandler handler;

    private ListView surveyList;
    private SurveyAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_survey_list, null);

        handler = new DbHandler(getActivity().getContentResolver());

        surveyList = (ListView) view.findViewById(R.id.surveyList);
        surveyList.setOnItemClickListener(this);

        adapter = new SurveyAdapter(getActivity());

        surveyList.setAdapter(adapter);

        getSurveys();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            getSurveys();
        }
    }

    private void getSurveys() {
        Location location = lm.getLocation();
        if (location != null) {
            getLocalSurveys();
            apiFacade.getSurveys(getActivity(), DEFAULT_LANG, location.getLatitude(), location.getLongitude());
        } else {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);

            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    L.i(TAG, "Location Updated!");
                    ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                    getLocalSurveys();
                    apiFacade.getSurveys(getActivity(), DEFAULT_LANG, location.getLatitude(), location.getLongitude());
                }
            });
        }
    }

    private void getLocalSurveys() {
        handler.startQuery(SurveyDbSchema.Query.TOKEN_QUERY, null, SurveyDbSchema.CONTENT_URI,
                SurveyDbSchema.Query.PROJECTION, null, null, SurveyDbSchema.SORT_ORDER_DESC);
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case SurveyDbSchema.Query.TOKEN_QUERY:
                    ArrayList<Survey> tasks = new ArrayList<Survey>();

                    if (cursor != null) {
                        cursor.moveToFirst();
                        do {
                            tasks.add(Survey.fromCursor(cursor));
                        } while (cursor.moveToNext());

                        cursor.close();
                    }

                    adapter.setData(tasks);
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.GET_SURVEYS_OPERATION_TAG.equals(operation.getTag())) {
                getLocalSurveys();
            }
        } else {
            L.i(TAG, "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Survey survey = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), SurveysTaskListActivity.class);
        intent.putExtra(Keys.SURVEY_ID, survey.getId());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().setTitle(R.string.survey_list_title);

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