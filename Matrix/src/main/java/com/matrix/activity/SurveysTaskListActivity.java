package com.matrix.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.adapter.MyTaskAdapter;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Task;
import com.matrix.helpers.APIFacade;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.L;

import java.util.ArrayList;
import java.util.Random;

public class SurveysTaskListActivity extends BaseActivity implements OnItemClickListener, NetworkOperationListenerInterface {
    private static final String TAG = SurveysTaskListActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();

    private Integer surveyId;

    private AsyncQueryHandler handler;

    private ListView taskList;
    private MyTaskAdapter adapter;
    private TextView responseTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_surveys_task_list);
        setTitle(R.string.surveys_task_list_title);

        if (getIntent() != null) {
            surveyId = getIntent().getIntExtra(Keys.SURVEY_ID, 0);
        }

        handler = new DbHandler(getContentResolver());

        taskList = (ListView) findViewById(R.id.taskList);
        taskList.setOnItemClickListener(this);

        responseTextView = (TextView) findViewById(R.id.responseTextView);

        adapter = new MyTaskAdapter(this);
        taskList.setAdapter(adapter);

        //createTasks(2, surveyId);
        getTasks(surveyId);
        //apiFacade.getSurveysTask(this, surveyId);
    }

    private void getTasks(long surveyId) {
        handler.startQuery(TaskDbSchema.Query.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.PROJECTION, TaskDbSchema.Columns.SURVEY_ID + "=?",
                new String[]{String.valueOf(surveyId)},
                TaskDbSchema.SORT_ORDER_DESC);
    }

    /*private void createTasks(int count, long surveyId) {
        for (int i = 0; i < count; i++) {
            Task task = new Task();
            task.setRandomId();
            task.setSurveyId(surveyId);
            task.setLatitude(50 + (new Random().nextDouble() / 10));
            task.setLongitude(30 + (new Random().nextDouble() / 10));
            task.setName("Survey: " + surveyId + " Task: " + i);
            task.setDescription("Task description " + i + "; Task description " + i);

            handler.startInsert(TaskDbSchema.Query.TOKEN_INSERT, null, TaskDbSchema.CONTENT_URI,
                    task.toContentValues());
        }
        getTasks(surveyId);
    }*/

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.TOKEN_QUERY:
                    ArrayList<Task> tasks = new ArrayList<Task>();

                    if (cursor != null && cursor.getCount()>0) {
                        cursor.moveToFirst();
                        do {
                            tasks.add(Task.fromCursor(cursor));
                        } while (cursor.moveToNext());

                        cursor.close();
                    }

                    responseTextView.setText("From local DB. Count:" + tasks.size());

                    adapter.setData(tasks);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.GET_SURVEYS_TASKS_OPERATION_TAG.equals(operation.getTag())) {
                getTasks(surveyId);
            }
        } else {
            L.i(TAG, "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = adapter.getItem(position);

        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, task.getId());
        startActivity(intent);
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

    @Override
    public void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }
}