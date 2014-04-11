package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Survey;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

/**
 * Activity for view Task detail information
 */
public class SurveyDetailsActivity extends BaseActivity implements View.OnClickListener {
    //private static final String TAG = SurveyDetailsActivity.class.getSimpleName();
    private AsyncQueryHandler handler;

    private Task nearTask = new Task();
    private Survey survey = new Survey();

    private TextView projectName;

    private TextView startTimeTextView;
    private TextView deadlineTimeTextView;
    private TextView durationTextView;

    private TextView projectPrice;
    private TextView projectExp;
    private TextView projectLocations;

    private LinearLayout descriptionLayout;
    private TextView projectDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_survey_details);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            survey = (Survey) getIntent().getSerializableExtra(Keys.SURVEY);
        }

        handler = new DbHandler(getContentResolver());

        projectName = (TextView) findViewById(R.id.projectName);

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        deadlineTimeTextView = (TextView) findViewById(R.id.deadlineTimeTextView);
        durationTextView = (TextView) findViewById(R.id.durationTextView);

        projectPrice = (TextView) findViewById(R.id.projectPrice);
        projectExp = (TextView) findViewById(R.id.projectExp);
        projectLocations = (TextView) findViewById(R.id.projectLocations);

        descriptionLayout = (LinearLayout) findViewById(R.id.descriptionLayout);
        projectDescription = (TextView) findViewById(R.id.projectDescription);

        findViewById(R.id.hideAllTasksButton).setOnClickListener(this);
        findViewById(R.id.showAllTasksButton).setOnClickListener(this);
        findViewById(R.id.mapImageView).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSurveyData(survey);

        TasksBL.getTaskFromDBbyID(handler, survey.getNearTaskId());

    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    nearTask = TasksBL.convertCursorToTask(cursor);

                    //setTaskData(nearTask);
                    break;
                default:
                    break;
            }
        }
    }

    public void setSurveyData(Survey survey) {
        projectName.setText(survey.getName());
        projectDescription.setText(survey.getDescription());
        descriptionLayout.setVisibility(TextUtils.isEmpty(survey.getDescription()) ? View.GONE : View.VISIBLE);

        long startTimeInMillisecond = UIUtils.isoTimeToLong(survey.getStartDateTime());
        long endTimeInMillisecond = UIUtils.isoTimeToLong(survey.getEndDateTime());
        long durationInMillisecond = endTimeInMillisecond - startTimeInMillisecond;

        startTimeTextView.setText(UIUtils.longToString(startTimeInMillisecond, 3));
        deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
        durationTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, durationInMillisecond));

        projectPrice.setText(getString(R.string.hk) + String.format(Locale.US, "%.1f", survey.getNearTaskPrice()));

        //TODO Set EXP
        projectExp.setText(String.valueOf(0));

        projectLocations.setText(String.valueOf(survey.getTaskCount()));

        //TODO Get survey type from server
        getSupportActionBar().setIcon(UIUtils.getSurveyTypeIcon(1));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hideAllTasksButton:
                nearTask.setIsHide(true);
                //setButtonsSettings(nearTask);
                TasksBL.setHideAllProjectTasksOnMapByID(handler, survey.getId(), true);
                break;
            case R.id.showAllTasksButton:
                TasksBL.setHideAllProjectTasksOnMapByID(handler, survey.getId(), false);
                nearTask.setIsHide(false);
                //setButtonsSettings(nearTask);
                break;
            case R.id.mapImageView:
                Bundle bundle = new Bundle();
                bundle.putInt(Keys.MAP_VIEWITEM_ID, nearTask.getSurveyId());
                bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.SURVEYTASKS.toString());
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.survey_detail_title);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
