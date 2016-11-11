package com.ros.smartrocket.db.entity;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.AnswerDbSchema;

import java.io.Serializable;

public class Answer extends BaseEntity implements Serializable {
    @SkipFieldInContentValues
    private static final long serialVersionUID = -4706526633427191907L;

    @SerializedName("MissionId")
    private Integer missionId;
    @SerializedName("QuestionId")
    private Integer questionId;
    @SerializedName("TaskId")
    private Integer taskId;
    @SerializedName("ProductId")
    private Integer productId;
    @SerializedName("Answer")
    private String answer;

    @SerializedName("Value")
    private String value;
    @SerializedName("Routing")
    private Integer routing;

    @SerializedName("Checked")
    private Boolean checked = false;

    private transient String fileUri;
    private transient Long fileSizeB;

    private transient String fileName;

    private transient Double longitude;
    private transient Double latitude;

    public Answer() {
    }

    public static Answer fromCursor(Cursor c) {
        Answer result = new Answer();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(AnswerDbSchema.Query._ID));
            result.setId(c.getInt(AnswerDbSchema.Query.ID));
            result.setQuestionId(c.getInt(AnswerDbSchema.Query.QUESTION_ID));
            result.setTaskId(c.getInt(AnswerDbSchema.Query.TASK_ID));
            result.setMissionId(c.getInt(AnswerDbSchema.Query.MISSION_ID));
            result.setAnswer(c.getString(AnswerDbSchema.Query.ANSWER));
            result.setValue(c.getString(AnswerDbSchema.Query.VALUE));
            result.setRouting(c.getInt(AnswerDbSchema.Query.ROUTING));
            result.setChecked(c.getInt(AnswerDbSchema.Query.CHECKED) == 1);
            result.setFileUri(c.getString(AnswerDbSchema.Query.FILE_URI));
            result.setFileSizeB(c.getLong(AnswerDbSchema.Query.FILE_SIZE_B));
            result.setFileName(c.getString(AnswerDbSchema.Query.FILE_NAME));
            result.setLongitude(c.getDouble(AnswerDbSchema.Query.LONGITUDE));
            result.setLatitude(c.getDouble(AnswerDbSchema.Query.LATITUDE));
            result.setProductId(c.getInt(AnswerDbSchema.Query.PRODUCT_ID));
        }

        return result;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getRouting() {
        return routing;
    }

    public void setRouting(Integer routing) {
        this.routing = routing;
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

    public void toggleChecked() {
        checked = !checked;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }


    public Long getFileSizeB() {
        return fileSizeB;
    }

    public void setFileSizeB(Long fileSizeB) {
        this.fileSizeB = fileSizeB;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}