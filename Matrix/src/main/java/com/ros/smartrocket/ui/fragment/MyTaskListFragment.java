package com.ros.smartrocket.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.ui.adapter.MyTaskAdapter;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.BaseFragment;
import com.ros.smartrocket.utils.eventbus.UploadProgressEvent;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import de.greenrobot.event.EventBus;

public class MyTaskListFragment extends BaseFragment implements OnItemClickListener, NetworkOperationListenerInterface,
        View.OnClickListener {
    private static final String TAG = MyTaskListFragment.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ImageView refreshButton;
    private AsyncQueryHandler handler;
    private MyTaskAdapter adapter;
    private TextView emptyListLTextView;

    public MyTaskListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_my_task_list, null);

        initActionBarView();
        refreshIconState(true);

        handler = new DbHandler(getActivity().getContentResolver());
        adapter = new MyTaskAdapter(getActivity());

        emptyListLTextView = (TextView) view.findViewById(R.id.emptyListLTextView);
        emptyListLTextView.setText(R.string.loading_missions);

        ListView taskList = (ListView) view.findViewById(R.id.taskList);
        taskList.setEmptyView(emptyListLTextView);
        taskList.setOnItemClickListener(this);
        taskList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            getMyTasks(true);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            getMyTasks(false);
        }
    }

    private void getMyTasks(boolean updateFromServer) {
        AllTaskFragment.stopRefreshProgress = !updateFromServer;
        refreshIconState(true);
        TasksBL.getMyTasksFromDB(handler);

        if (updateFromServer) {
            if (UIUtils.isOnline(getActivity())) {
                ((BaseActivity) getActivity()).sendNetworkOperation(apiFacade.getMyTasksOperation());
            } else {
                refreshIconState(false);
                UIUtils.showSimpleToast(getActivity(), R.string.no_internet);
            }
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
                    adapter.setData(TasksBL.convertCursorToTasksList(cursor));
                    if (AllTaskFragment.stopRefreshProgress) {
                        if (adapter.getCount() == 0) {
                            emptyListLTextView.setText(R.string.you_have_no_tasks);
                        }
                        refreshIconState(false);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
            AllTaskFragment.stopRefreshProgress = true;
            TasksBL.getMyTasksFromDB(handler);
            IntentUtils.refreshMainMenuMyTaskCount(getActivity());
        }

    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
            if (operation.getResponseStatusCode() == BaseNetworkService.DEVICE_INTEERNAL_ERROR) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                L.i(TAG, operation.getResponseError());
                refreshIconState(false);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = adapter.getItem(position);

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case SCHEDULED:
                startActivity(IntentUtils.getTaskValidationIntent(getActivity(), task.getId(), task.getMissionId(),
                        false, false));
                break;
            /*case RE_DO_TASK:
                startActivity(IntentUtils.getQuestionsIntent(getActivity(), task.getId()));
                break;*/
            default:
                startActivity(IntentUtils.getTaskDetailIntent(getActivity(), task.getId(), task.getMissionId(),
                        task.getStatusId(), TasksBL.isPreClaimTask(task)));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshButton:
                refreshData();
                break;
            default:
                break;
        }
    }

    private void refreshData() {
        getMyTasks(true);
        IntentUtils.refreshProfileAndMainMenu(getActivity());
        IntentUtils.refreshMainMenuMyTaskCount(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        initActionBarView();

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initActionBarView() {
        if (refreshButton == null) {
            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            View view = actionBar.getCustomView();
            if (view != null) {
                initRefreshButton(actionBar);
            } else {
                actionBar.setCustomView(R.layout.actionbar_custom_view_all_task);
                initRefreshButton(actionBar);
            }
        }
    }

    public void initRefreshButton(ActionBar actionBar) {
        View view = actionBar.getCustomView();
        refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
        if (refreshButton != null) {
            refreshButton.setOnClickListener(this);
        }
    }

    private void refreshIconState(boolean isLoading) {
        if (refreshButton != null && getActivity() != null) {
            if (isLoading) {
                refreshButton.setClickable(false);
                refreshButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
            } else {
                refreshButton.setClickable(true);
                refreshButton.clearAnimation();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        ((BaseActivity) getActivity()).addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        ((BaseActivity) getActivity()).removeNetworkOperationListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(UploadProgressEvent event) {
        if (isVisible() && adapter != null) {
            adapter.notifyDataSetChanged();
            if (event.isDone()) {
                refreshData();
            }
        }
    }
}
