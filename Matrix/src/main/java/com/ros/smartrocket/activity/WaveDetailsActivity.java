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
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for view Task detail information
 */
public class WaveDetailsActivity extends BaseActivity implements View.OnClickListener {
    private Calendar calendar = Calendar.getInstance();
    private AsyncQueryHandler handler;

    private Task nearTask = new Task();
    private Wave wave = new Wave();

    private TextView startTimeTextView;
    private TextView deadlineTimeTextView;
    private TextView dueTextView;

    private TextView projectPrice;
    private TextView projectExp;
    private TextView projectLocations;
    private TextView textQuestionsCount;
    private TextView photoQuestionsCount;

    private LinearLayout descriptionLayout;
    private TextView projectDescription;

    public WaveDetailsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_wave_details);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            wave = (Wave) getIntent().getSerializableExtra(Keys.WAVE);
        }

        handler = new DbHandler(getContentResolver());

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        deadlineTimeTextView = (TextView) findViewById(R.id.deadlineTimeTextView);
        dueTextView = (TextView) findViewById(R.id.dueTextView);

        projectPrice = (TextView) findViewById(R.id.projectPrice);
        projectExp = (TextView) findViewById(R.id.projectExp);
        projectLocations = (TextView) findViewById(R.id.projectLocations);
        textQuestionsCount = (TextView) findViewById(R.id.textQuestionsCount);
        photoQuestionsCount = (TextView) findViewById(R.id.photoQuestionsCount);

        descriptionLayout = (LinearLayout) findViewById(R.id.descriptionLayout);
        projectDescription = (TextView) findViewById(R.id.projectDescription);

        findViewById(R.id.hideAllTasksButton).setOnClickListener(this);
        findViewById(R.id.showAllTasksButton).setOnClickListener(this);
        findViewById(R.id.mapImageView).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWaveData(wave);

        TasksBL.getTaskFromDBbyID(handler, wave.getNearTaskId());

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

                    break;
                default:
                    break;
            }
        }
    }

    public void setWaveData(Wave wave) {
        projectDescription.setText(wave.getDescription());
        descriptionLayout.setVisibility(TextUtils.isEmpty(wave.getDescription()) ? View.GONE : View.VISIBLE);

        long startTimeInMillisecond = UIUtils.isoTimeToLong(wave.getStartDateTime());
        long endTimeInMillisecond = UIUtils.isoTimeToLong(wave.getEndDateTime());
        long timeoutInMillisecond = wave.getLongExpireTimeoutForClaimedTask();

        startTimeTextView.setText(UIUtils.longToString(startTimeInMillisecond, 3));
        deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
        dueTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, timeoutInMillisecond));

        projectPrice.setText(UIUtils.getBalanceOrPrice(this, wave.getNearTaskPrice()));
        projectExp.setText(String.format(Locale.US, "%.0f", wave.getExperienceOffer()));
        projectLocations.setText(String.valueOf(wave.getTaskCount()));
        textQuestionsCount.setText(String.valueOf(wave.getNoPhotoQuestionsCount()));
        photoQuestionsCount.setText(String.valueOf(wave.getPhotoQuestionsCount()));

        //TODO Get wave type from server
        getSupportActionBar().setIcon(UIUtils.getWaveTypeActionBarIcon(1));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hideAllTasksButton:
                nearTask.setIsHide(true);
                TasksBL.setHideAllProjectTasksOnMapByID(handler, wave.getId(), true);
                break;
            case R.id.showAllTasksButton:
                TasksBL.setHideAllProjectTasksOnMapByID(handler, wave.getId(), false);
                nearTask.setIsHide(false);
            case R.id.mapImageView:
                Bundle bundle = new Bundle();
                bundle.putInt(Keys.MAP_VIEW_ITEM_ID, nearTask.getWaveId());
                bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.WAVE_TASKS.toString());
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
        ((TextView) view.findViewById(R.id.titleTextView)).setText(wave.getName());

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
