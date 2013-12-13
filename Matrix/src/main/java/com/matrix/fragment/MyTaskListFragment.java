package com.matrix.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.activity.TaskDetailsActivity;
import com.matrix.adapter.MyTaskAdapter;
import com.matrix.bl.TasksBL;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Task;
import com.matrix.helpers.APIFacade;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.L;

import java.util.ArrayList;

public class MyTaskListFragment extends Fragment implements OnItemClickListener, NetworkOperationListenerInterface {
    private static final String TAG = MyTaskListFragment.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ViewGroup view;

    private AsyncQueryHandler handler;

    private ListView taskList;
    private MyTaskAdapter adapter;
    private TextView responseTextView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_my_task_list, null);

        handler = new DbHandler(getActivity().getContentResolver());

        taskList = (ListView) view.findViewById(R.id.taskList);
        taskList.setOnItemClickListener(this);

        adapter = new MyTaskAdapter(getActivity());

        taskList.setAdapter(adapter);

        TasksBL.getMyTasksFromDB(handler);
        apiFacade.getMyTasks(getActivity());

        ((BaseActivity) getActivity()).getSupportActionBar().setSupportProgressBarIndeterminateVisibility(false);
        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            TasksBL.getMyTasksFromDB(handler);
            apiFacade.getMyTasks(getActivity());
        }
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    ArrayList<Task> tasks = TasksBL.convertCursorToTasksList(cursor);
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
            if (Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
                TasksBL.getMyTasksFromDB(handler);
            }
        } else {
            L.i(TAG, "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, task.getId());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().setTitle(R.string.my_tasks_title);

        super.onCreateOptionsMenu(menu, inflater);
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
