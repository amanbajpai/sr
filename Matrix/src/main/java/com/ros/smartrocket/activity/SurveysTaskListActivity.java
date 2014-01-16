package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.MyTaskAdapter;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

public class SurveysTaskListActivity extends BaseActivity implements OnItemClickListener, NetworkOperationListenerInterface {
    //private static final String TAG = SurveysTaskListActivity.class.getSimpleName();
    //private APIFacade apiFacade = APIFacade.getInstance();

    private Integer surveyId;

    private AsyncQueryHandler handler;

    private ListView taskList;
    private MyTaskAdapter adapter;

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

        adapter = new MyTaskAdapter(this);
        taskList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTasks(surveyId);
    }

    private void getTasks(long surveyId) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.SURVEY_ID + "=?",
                new String[]{String.valueOf(surveyId)},
                TaskDbSchema.SORT_ORDER_DESC);
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    ArrayList<Task> tasks = new ArrayList<Task>();

                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {
                            tasks.add(Task.fromCursor(cursor));
                        } while (cursor.moveToNext());

                        cursor.close();
                    }

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
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = adapter.getItem(position);

        if (task.getStatusId() >= Task.TaskStatusId.validation.getStatusId()) {
            startActivity(IntentUtils.getTaskValidationIntent(this, task.getId()));
        } else {
            startActivity(IntentUtils.getTaskDetailIntent(this, task.getId()));
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
