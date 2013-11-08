package com.matrix.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.matrix.BaseActivity;
import com.matrix.R;
import com.matrix.adapter.TaskAdapter;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Task;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.net.WSUrl;
import com.matrix.utils.L;

import java.util.ArrayList;

public class AllTaskListFragment extends Fragment implements OnClickListener, OnItemClickListener, NetworkOperationListenerInterface {
    private static final String TAG = AllTaskListFragment.class.getSimpleName();
    private static final String GET_TASKS_OPERATION_TAG = "get_tasks_operation_tag";
    private ViewGroup view;

    private AsyncQueryHandler handler;

    public ListView taskList;
    public TaskAdapter adapter;
    public TextView responseTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_all_task_list, null);

        getActivity().setTitle(R.string.all_tasks_title);

        handler = new DbHandler(getActivity().getContentResolver());

        taskList = (ListView) view.findViewById(R.id.taskList);
        responseTextView = (TextView) view.findViewById(R.id.responseTextView);
        view.findViewById(R.id.getTasksButton).setOnClickListener(this);

        adapter = new TaskAdapter(getActivity());

        taskList.setAdapter(adapter);

        getTasks();
        getTasksFromServer();

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            //TODO Move to fragment second time
        }
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
        ((BaseActivity) getActivity()).sendNetworkOperation(operation);
    }


    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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