package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.MyTaskAdapter;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;

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

        getMyTasks();

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            getMyTasks();
        }
    }

    private void getMyTasks() {
        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);

        TasksBL.getMyTasksFromDB(handler);
        apiFacade.getMyTasks(getActivity());
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
            L.i(TAG, operation.getResponseError());
        }

        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = adapter.getItem(position);

        startActivity(IntentUtils.getTaskDetailIntent(getActivity(), task.getId()));
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
