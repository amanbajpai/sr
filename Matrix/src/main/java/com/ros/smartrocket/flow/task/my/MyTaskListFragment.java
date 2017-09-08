package com.ros.smartrocket.flow.task.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.BaseFragment;
import com.ros.smartrocket.flow.task.AllTaskFragment;
import com.ros.smartrocket.flow.task.TaskMvpView;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.adapter.MyTaskAdapter;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.eventbus.UploadProgressEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;

public class MyTaskListFragment extends BaseFragment implements OnItemClickListener, TaskMvpView {
    @BindView(R.id.emptyListLTextView)
    CustomTextView emptyListLTextView;
    @BindView(R.id.taskList)
    ListView taskList;

    private Unbinder unbinder;
    private ImageView refreshButton;
    private MyTaskAdapter adapter;
    private MyTaskMvpPresenter<TaskMvpView> presenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_my_task_list, null);
        unbinder = ButterKnife.bind(this, view);
        initUI();
        presenter = new MyTaskPresenter<>();
        presenter.attachView(this);
        refreshIconState(true);
        return view;
    }

    private void initUI() {
        initActionBarView();
        refreshIconState(true);
        adapter = new MyTaskAdapter(getActivity());
        emptyListLTextView.setText(R.string.loading_missions);
        taskList.setEmptyView(emptyListLTextView);
        taskList.setOnItemClickListener(this);
        taskList.setAdapter(adapter);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getMyTasks(boolean updateFromServer) {
        AllTaskFragment.stopRefreshProgress = !updateFromServer;
        refreshIconState(true);
        presenter.loadMyTasksFromDb();
        if (updateFromServer) {
            if (UIUtils.isOnline(getActivity())) {
                presenter.getMyTasksFromServer();
            } else {
                refreshIconState(false);
                UIUtils.showSimpleToast(getActivity(), R.string.no_internet);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        if (networkError.getErrorCode() == NetworkError.DEVICE_INTEERNAL_ERROR) {
            if (getActivity() != null) getActivity().finish();
        } else {
            UIUtils.showSimpleToast(getActivity(), networkError.getErrorMessageRes());
        }
    }

    @Override
    public void onTaskLoadingComplete(List<Task> list) {
        adapter.setData(list);
        if (AllTaskFragment.stopRefreshProgress) {
            if (adapter.getCount() == 0) emptyListLTextView.setText(R.string.you_have_no_tasks);
            refreshIconState(false);
        }
    }

    @Override
    public void onTasksLoaded() {
        AllTaskFragment.stopRefreshProgress = true;
        presenter.loadMyTasksFromDb();
        IntentUtils.refreshMainMenuMyTaskCount(getActivity());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = adapter.getItem(position);
        if (TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId.SCHEDULED)
            startActivity(IntentUtils
                    .getTaskValidationIntent(getActivity(),
                            task.getId(),
                            task.getMissionId(),
                            false,
                            false));
        else
            startActivity(IntentUtils
                    .getTaskDetailIntent(getActivity(),
                            task.getId(),
                            task.getMissionId(),
                            task.getStatusId(),
                            TasksBL.isPreClaimTask(task)));
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
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (refreshButton == null && actionBar != null) {
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
            refreshButton.setOnClickListener(v -> refreshData());
        }
    }

    @Override
    public void refreshIconState(boolean isLoading) {
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
    public void onStop() {
        presenter.detachView();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.attachView(this);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(UploadProgressEvent event) {
        if (isVisible() && adapter != null) {
            adapter.notifyDataSetChanged();
            if (event.isDone()) refreshData();
        }
    }
}
