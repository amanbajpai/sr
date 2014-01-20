package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Survey;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

/**
 * Activity for view Task detail information
 */
public class SurveyDetailsActivity extends BaseActivity implements View.OnClickListener {
    //private static final String TAG = SurveyDetailsActivity.class.getSimpleName();
    //private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    private AsyncQueryHandler handler;

    private Task nearTask = new Task();
    private Survey survey = new Survey();

    private TextView projectName;
    private TextView startTimeTextView;
    private TextView deadlineTimeTextView;
    private TextView expiryTimeTextView;
    private TextView projectPrice;
    private TextView projectExp;
    private TextView projectLocations;
    private TextView taskDescription;
    private TextView taskComposition;
    private Button hideAllTasksButton;
    private Button showAllTasksButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_survey_details);
        setTitle(R.string.survey_detail_title);

        if (getIntent() != null) {
            survey = (Survey) getIntent().getSerializableExtra(Keys.SURVEY);
        }

        handler = new DbHandler(getContentResolver());

        projectName = (TextView) findViewById(R.id.projectName);
        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        deadlineTimeTextView = (TextView) findViewById(R.id.deadlineTimeTextView);
        expiryTimeTextView = (TextView) findViewById(R.id.expiryTimeTextView);
        projectPrice = (TextView) findViewById(R.id.projectPrice);
        projectExp = (TextView) findViewById(R.id.projectExp);
        projectLocations = (TextView) findViewById(R.id.projectLocations);
        taskDescription = (TextView) findViewById(R.id.taskDescription);
        taskComposition = (TextView) findViewById(R.id.taskComposition);

        hideAllTasksButton = (Button) findViewById(R.id.hideAllTasksButton);
        hideAllTasksButton.setOnClickListener(this);
        showAllTasksButton = (Button) findViewById(R.id.showAllTasksButton);
        showAllTasksButton.setOnClickListener(this);

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

                    setTaskData(nearTask);
                    break;
                default:
                    break;
            }
        }
    }

    public void setTaskData(Task task) {
        taskDescription.setText(task.getDescription());
        taskComposition.setText("-");

        setButtonsSettings(task);
    }

    public void setSurveyData(Survey survey) {
        projectName.setText(survey.getName());

        long startTimeInMillisecond = UIUtils.isoTimeToLong(survey.getStartDateTime());
        long endTimeInMillisecond = UIUtils.isoTimeToLong(survey.getEndDateTime());

        startTimeTextView.setText(UIUtils.longToString(startTimeInMillisecond, 3));
        deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
        //TODO Set expiry time
        expiryTimeTextView.setText("-");

        projectPrice.setText(Html.fromHtml(String.format(getString(R.string.survey_price_detail), String.format(Locale.US, "%.1f",
                survey.getNearTaskPrice()))));
        //TODO Set EXP
        projectExp.setText(Html.fromHtml(String.format(getString(R.string.survey_exp_detail), String.format(Locale.US, "%,d", 130))));
        projectLocations.setText(Html.fromHtml(String.format(getString(R.string.survey_locations), survey.getTaskCount())));
    }

    public void setButtonsSettings(Task task) {
        if (UIUtils.isTrue(task.getIsHide())) {
            hideAllTasksButton.setVisibility(View.GONE);
            showAllTasksButton.setVisibility(View.VISIBLE);
        } else {
            hideAllTasksButton.setVisibility(View.VISIBLE);
            showAllTasksButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hideAllTasksButton:
                nearTask.setIsHide(true);
                setButtonsSettings(nearTask);
                TasksBL.setHideAllProjectTasksOnMapByID(handler, survey.getId(), true);
                break;
            case R.id.showAllTasksButton:
                TasksBL.setHideAllProjectTasksOnMapByID(handler, survey.getId(), false);
                nearTask.setIsHide(false);
                setButtonsSettings(nearTask);
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
