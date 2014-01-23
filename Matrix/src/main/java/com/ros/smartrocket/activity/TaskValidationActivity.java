package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.dialog.DefaultInfoDialog;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.Locale;

public class TaskValidationActivity extends BaseActivity implements View.OnClickListener {
    //private static final String TAG = TaskValidationActivity.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private TextView expiryDateTextView;
    private TextView expiryTimeTextView;
    private TextView taskDataSizeTextView;

    private Integer taskId;
    private Task task = new Task();

    private AsyncQueryHandler handler;
    private ArrayList<NotUploadedFile> notUploadedFiles = new ArrayList<NotUploadedFile>();
    private float filesSizeB = 0;

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

                    long endDateTime = UIUtils.isoTimeToLong(task.getEndDateTime());

                    notUploadedFiles = AnswersBL.getTaskFilesListToUpload(task.getId(), endDateTime);
                    filesSizeB = AnswersBL.getTaskFilesSizeMb(notUploadedFiles);

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
        taskDataSizeTextView.setText(String.format(Locale.US, "%.1f", filesSizeB / 1024));
    }

    public void setFilesToUploadDbAndStartUpload(Boolean use3G) {
        //Add files data to DB and start upload
        for (NotUploadedFile notUploadedFile : notUploadedFiles) {
            notUploadedFile.setUse3G(use3G);
            FilesBL.insertNotUploadedFile(notUploadedFile);
        }
        startService(new Intent(TaskValidationActivity.this, UploadFileService.class).setAction(Keys
                .ACTION_CHECK_NOT_UPLOADED_FILES));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recheckTaskButton:
                DialogUtils.showReCheckAnswerTaskDialog(this, task.getSurveyId(), task.getId());
                break;
            case R.id.sendNowButton:
                TasksBL.updateTaskStatusId(taskId, Task.TaskStatusId.validation.getStatusId());

                if (filesSizeB / 1024 > preferencesManager.get3GUploadTaskLimit()) {
                    DialogUtils.show3GLimitExceededDialog(this, new DefaultInfoDialog.DialogButtonClickListener() {
                        @Override
                        public void onLeftButtonPressed(Dialog dialog) {
                            dialog.dismiss();
                            setFilesToUploadDbAndStartUpload(false);
                            finish();
                        }

                        @Override
                        public void onRightButtonPressed(Dialog dialog) {
                            dialog.dismiss();
                            setFilesToUploadDbAndStartUpload(true);
                            finish();
                        }
                    });
                } else {
                    setFilesToUploadDbAndStartUpload(true);
                    finish();
                }
                break;
            case R.id.sendLaterButton:
                finish();
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
