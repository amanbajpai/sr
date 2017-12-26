package com.ros.smartrocket.db.entity.file;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class TaskFileToUpload extends FileToUpload {
    @SkipFieldInContentValues
    private static final long serialVersionUID = 5410835468659163928L;
    @SerializedName("TaskId")
    private Integer taskId;
    @SerializedName("MissionId")
    private Integer missionId;
    @SerializedName("QuestionId")
    private Integer questionId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }


}
