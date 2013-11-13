package com.matrix.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.activity.SurveysTaskListActivity;
import com.matrix.adapter.SurveyAdapter;
import com.matrix.db.SurveyDbSchema;
import com.matrix.db.entity.Survey;
import com.matrix.helpers.APIFacade;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.L;

import java.util.ArrayList;

/**
 * Fragment - display all tasks in {@link android.widget.ListView}
 */
public class SurveyListFragment extends Fragment implements OnClickListener, OnItemClickListener, NetworkOperationListenerInterface {
    private static final String TAG = SurveyListFragment.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ViewGroup view;

    private AsyncQueryHandler handler;

    public ListView surveyList;
    public SurveyAdapter adapter;
    public TextView responseTextView;

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

        responseTextView = (TextView) view.findViewById(R.id.responseTextView);
        view.findViewById(R.id.getSurveysButton).setOnClickListener(this);
        view.findViewById(R.id.addSurveysButton).setOnClickListener(this);

        adapter = new SurveyAdapter(getActivity());

        surveyList.setAdapter(adapter);

        getSurveys();
        apiFacade.getSurveys(getActivity());

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            //TODO Move to fragment second time
        }
    }

    private void getSurveys() {
        handler.startQuery(SurveyDbSchema.Query.TOKEN_QUERY, null, SurveyDbSchema.CONTENT_URI,
                SurveyDbSchema.Query.PROJECTION, null, null, SurveyDbSchema.SORT_ORDER_DESC);
    }

    private void createSurveys(int count) {
        for (int i = 0; i < count; i++) {
            Survey survey = new Survey();
            survey.setRandomId();
            survey.setName("Survey: " + i);
            survey.setDescription("Survey description " + i + "; Survey description " + i);

            handler.startInsert(SurveyDbSchema.Query.TOKEN_INSERT, null, SurveyDbSchema.CONTENT_URI,
                    survey.toContentValues());
        }
        getSurveys();
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

                    responseTextView.setText("From local DB. Count:" + tasks.size());

                    adapter.setData(tasks);
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.GET_SURVEYS_OPERATION_TAG.equals(operation.getTag())) {
                getSurveys();
            }
        } else {
            L.i(TAG, "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getSurveysButton:
                apiFacade.getSurveys(getActivity());
                break;
            case R.id.addSurveysButton:
                createSurveys(10);
                break;
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