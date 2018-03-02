package com.ros.smartrocket.utils.eventbus;

public class QuitQuestionFlowAction {
    private int taskId;
    private int missionId;

    public QuitQuestionFlowAction(int taskId, int missionId) {
        this.taskId = taskId;
        this.missionId = missionId;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getMissionId() {
        return missionId;
    }
}
