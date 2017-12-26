package com.ros.smartrocket.presentation.details.claim;

import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface ClaimMvpPresenter<V extends ClaimMvpView> extends MvpPresenter<V> {
    void claimTask();

    void unClaimTask();

    void unClaimTaskRequest();

    void startTask();

    void setTask(Task t);

    void downloadMedia();

}
