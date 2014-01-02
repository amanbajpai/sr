package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

public class TaskValidationActivity extends BaseActivity implements View.OnClickListener {
    public final static String TAG = TaskValidationActivity.class.getSimpleName();
    private TextView expiryDateTextView;
    private TextView expiryTimeTextView;
    private TextView taskDataSizeTextView;

    private Integer taskId;
    private Task task = new Task();

    private AsyncQueryHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_task_validation);
        setTitle(R.string.task_validation_title);

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
        }

        handler = new DbHandler(getContentResolver());

        expiryDateTextView = (TextView) findViewById(R.id.expiryDateTextView);
        expiryTimeTextView = (TextView) findViewById(R.id.expiryTimeTextView);
        taskDataSizeTextView = (TextView) findViewById(R.id.taskDataSizeTextView);

        findViewById(R.id.recheckTaskButton).setOnClickListener(this);
        findViewById(R.id.sendNowButton).setOnClickListener(this);
        findViewById(R.id.sendLaterButton).setOnClickListener(this);

        TasksBL.getTaskFromDBbyID(handler, taskId);
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    task = TasksBL.convertCursorToTask(cursor);

                    setTaskData(task);
                    SurveysBL.getSurveyFromDB(handler, task.getSurveyId());
                    break;

                default:
                    break;
            }
        }
    }

    public void setTaskData(Task task) {
        long expiryTimeLong = UIUtils.isoTimeToLong(task.getEndDateTime());

        expiryDateTextView.setText(UIUtils.longToString(expiryTimeLong, 1));
        expiryTimeTextView.setText(UIUtils.longToString(expiryTimeLong, 0));
        taskDataSizeTextView.setText(String.valueOf(22));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recheckTaskButton:

                break;
            case R.id.sendNowButton:

                break;
            case R.id.sendLaterButton:

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
