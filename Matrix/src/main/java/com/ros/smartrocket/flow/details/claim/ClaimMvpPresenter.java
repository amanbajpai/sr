package com.ros.smartrocket.flow.details.claim;

import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.MvpPresenter;

public interface ClaimMvpPresenter<V extends ClaimMvpView> extends MvpPresenter<V> {
    void claimTask();

    void unClaimTask();

    void unClaimTaskRequest();

    void startTask();

    void setTask(Task t);

    void downloadMedia();

}
