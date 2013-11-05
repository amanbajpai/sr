package com.matrix;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.adapter.TaskAdapter;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Task;
import com.matrix.location.LocationService;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.net.WSUrl;
import com.matrix.utils.L;
import com.matrix.utils.UIUtils;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final String GET_TASKS_OPERATION_TAG = "get_tasks_operation_tag";

    private AsyncQueryHandler handler;

    public ListView taskList;
    public TaskAdapter adapter;
    public TextView responseTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, LocationService.class));

        handler = new MessageHandler(getContentResolver());

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        taskList = (ListView) findViewById(R.id.taskList);
        responseTextView = (TextView) findViewById(R.id.responseTextView);
        findViewById(R.id.getTasksButton).setOnClickListener(this);

        adapter = new TaskAdapter(this);

        taskList.setAdapter(adapter);


        getTasks();
        getTasksFromServer();
    }

    private void getTasks() {
        handler.startQuery(TaskDbSchema.Query.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.PROJECTION, null, null, TaskDbSchema.SORT_ORDER_DESC);
    }

    private void getTasksFromServer() {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_TASKS);
        operation.setTag(GET_TASKS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        sendNetworkOperation(operation);
    }

    class MessageHandler extends AsyncQueryHandler {

        public MessageHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.TOKEN_QUERY:
                    ArrayList<Task> tasks = new ArrayList<Task>();

                    if (cursor != null) {
                        cursor.moveToFirst();

                        do {
                            tasks.add(Task.fromCursor(cursor));
                        } while (cursor.moveToNext());

                        cursor.close();
                    }

                    responseTextView.setText("tasks from local DB. Count:" + tasks.size());

                    adapter.setData(tasks);
                    break;
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            switch (token) {
                case TaskDbSchema.Query.TOKEN_UPDATE:

                    break;
            }
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            switch (token) {
                case TaskDbSchema.Query.TOKEN_INSERT:

                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (GET_TASKS_OPERATION_TAG.equals(operation.getTag())) {
                getTasks();
            }
        } else {
            L.i(TAG, "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getTasksButton:
                getTasksFromServer();
                break;
        }
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
