package com.ros.smartrocket.flow.details.claim;

import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.flow.base.NetworkMvpView;

public interface ClaimMvpView extends NetworkMvpView {
    void onTaskStarted(Task task);

    void onTaskUnclaimed();

    void showClaimDialog(String date);

    void showUnClaimDialog();

    void showDownloadMediaDialog(Wave wave);
}
