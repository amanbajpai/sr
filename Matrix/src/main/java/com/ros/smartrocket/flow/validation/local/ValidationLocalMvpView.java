package com.ros.smartrocket.flow.validation.local;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.NetworkMvpView;

import java.util.List;

public interface ValidationLocalMvpView extends NetworkMvpView {
    void onTaskLoadedFromDb(Task task);

    void onClosingStatementQuestionLoadedFromDB(List<Question> questions);

    void setTaskFilesSize(float size);

    void onTaskLocationSaved(Task task, boolean isSendNow);

    void onTaskLocationSavedError(Task task, String errorText);
}
