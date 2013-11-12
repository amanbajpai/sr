package com.matrix.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.MainActivity;
import com.matrix.R;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Task;
import com.matrix.helpers.APIFacade;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.UIUtils;
import com.matrix.views.SlidingUpPanelLayout;

public class TaskDetailsActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = TaskDetailsActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();

    private AsyncQueryHandler handler;

    public String taskId;
    public Task task = new Task();

    public TextView taskName;
    public TextView taskDescription;
    public SlidingUpPanelLayout slidingUpPanelLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_task_details);

        if (getIntent() != null) {
            taskId = getIntent().getStringExtra(Keys.TASK_ID);
        }

        setTitle(R.string.task_detail_title);

        handler = new DbHandler(getContentResolver());

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        slidingUpPanelLayout.setAnchorPoint(0.3f);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                /*if (slideOffset < 0.2) {
                    if (getSupportActionBar().isShowing()) {
                        getSupportActionBar().hide();
                    }
                } else {
                    if (!getSupportActionBar().isShowing()) {
                        getSupportActionBar().show();
                    }
                }*/
            }

            @Override
            public void onPanelExpanded(View panel) {


            }

            @Override
            public void onPanelCollapsed(View panel) {


            }

            @Override
            public void onPanelAnchored(View panel) {


            }
        });

        taskName = (TextView) findViewById(R.id.taskName);
        taskDescription = (TextView) findViewById(R.id.taskDescription);

        getTasks(taskId);
    }

    private void getTasks(String taskId) {
        handler.startQuery(TaskDbSchema.Query.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.PROJECTION, TaskDbSchema.Columns.ID + "=?", new String[]{taskId}, TaskDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.TOKEN_QUERY:
                    if (cursor != null) {
                        cursor.moveToFirst();

                        task = Task.fromCursor(cursor);
                        setData();

                        cursor.close();
                    }
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {

            }
        } else {
            UIUtils.showSimpleToast(TaskDetailsActivity.this, "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    public void setData() {
        taskName.setText(task.getName());
        taskDescription.setText(task.getDescription());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                //TODO Delete
                /*String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                apiFacade.login(this, email, password);*/
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerButton:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    protected void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }
}
