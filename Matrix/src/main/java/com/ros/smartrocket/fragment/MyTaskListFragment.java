package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.adapter.MyTaskAdapter;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.util.List;

/**
 * Fragment - display my tasks in {@link android.widget.ListView}
 */
public class MyTaskListFragment extends Fragment implements OnItemClickListener, NetworkOperationListenerInterface,
        View.OnClickListener {
    private static final String TAG = MyTaskListFragment.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ImageView refreshButton;
    private AsyncQueryHandler handler;
    private MyTaskAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_my_task_list, null);

        handler = new DbHandler(getActivity().getContentResolver());
        adapter = new MyTaskAdapter(getActivity());

        TextView emptyListLTextView = (TextView) view.findViewById(R.id.emptyListLTextView);

        ListView taskList = (ListView) view.findViewById(R.id.taskList);
        taskList.setEmptyView(emptyListLTextView);
        taskList.setOnItemClickListener(this);
        taskList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getMyTasks();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            getMyTasks();
        }
    }

    private void getMyTasks() {
        refreshIconState(true);
        TasksBL.getMyTasksFromDB(handler);
        if (UIUtils.isOnline(getActivity())) {
            ((BaseActivity) getActivity()).sendNetworkOperation(apiFacade.getMyTasksOperation());
        } else {
            refreshIconState(false);
            UIUtils.showSimpleToast(getActivity(), R.string.no_internet);
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
                    List<Task> tasks = TasksBL.convertCursorToTasksList(cursor);
                    adapter.setData(tasks);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
                TasksBL.getMyTasksFromDB(handler);
            }
        } else {
            L.i(TAG, operation.getResponseError());
        }
        refreshIconState(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = adapter.getItem(position);

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case claimed:
            case started:
                startActivity(IntentUtils.getTaskDetailIntent(getActivity(), task.getId()));
                break;
            case scheduled:
                startActivity(IntentUtils.getTaskValidationIntent(getActivity(), task.getId(), false));
                break;
            case reDoTask:
                startActivity(IntentUtils.getQuestionsIntent(getActivity(), task.getSurveyId(), task.getId()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshButton:
                getMyTasks();
                IntentUtils.refreshProfileAndMainMenu(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();

        View view = actionBar.getCustomView();
        refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshIconState(boolean isLoading) {
        if (refreshButton != null && getActivity() != null) {
            if (isLoading) {
                refreshButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
            } else {
                refreshButton.clearAnimation();
            }
        }
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
