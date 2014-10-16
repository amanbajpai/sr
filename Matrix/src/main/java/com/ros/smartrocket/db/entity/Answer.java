package com.ros.smartrocket.db.entity;

import android.database.Cursor;

import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.utils.L;

import java.io.Serializable;

public class Answer extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    private Integer QuestionId;
    private Integer TaskId;
    private String Answer;

    private String Value;
    private Integer Routing;

    private Boolean Checked = false;

    private transient String FileUri;
    private transient Long FileSizeB;

    private transient String FileName;

    private transient Double Longitude;
    private transient Double Latitude;

    public Answer() {
    }

    public static Answer fromCursor(Cursor c) {
        Answer result = new Answer();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(AnswerDbSchema.Query._ID));
            result.setId(c.getInt(AnswerDbSchema.Query.ID));
            result.setQuestionId(c.getInt(AnswerDbSchema.Query.QUESTION_ID));
            result.setTaskId(c.getInt(AnswerDbSchema.Query.TASK_ID));
            result.setAnswer(c.getString(AnswerDbSchema.Query.ANSWER));
            result.setValue(c.getString(AnswerDbSchema.Query.VALUE));
            result.setRouting(c.getInt(AnswerDbSchema.Query.ROUTING));
            result.setChecked(c.getInt(AnswerDbSchema.Query.CHECKED) == 1);
            result.setFileUri(c.getString(AnswerDbSchema.Query.FILE_URI));
            result.setFileSizeB(c.getLong(AnswerDbSchema.Query.FILE_SIZE_B));
            result.setFileName(c.getString(AnswerDbSchema.Query.FILE_NAME));
            result.setLongitude(c.getDouble(AnswerDbSchema.Query.LONGITUDE));
            result.setLatitude(c.getDouble(AnswerDbSchema.Query.LATITUDE));
        }

        L.d("Answer", "Answer:" + result.toString());
        return result;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public boolean isChecked() {
        return Checked;
    }

    public void setChecked(boolean checked) {
        Checked = checked;
    }

    public Integer getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(Integer questionId) {
        QuestionId = questionId;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public Integer getRouting() {
        return Routing;
    }

    public void setRouting(Integer routing) {
        Routing = routing;
    }

    public Integer getTaskId() {
        return TaskId;
    }

    public void setTaskId(Integer taskId) {
        TaskId = taskId;
    }


    public void toggleChecked() {
        Checked = !Checked;
    }

    public String getFileUri() {
        return FileUri;
    }

    public void setFileUri(String fileUri) {
        this.FileUri = fileUri;
    }


    public Long getFileSizeB() {
        return FileSizeB;
    }

    public void setFileSizeB(Long fileSizeB) {
        this.FileSizeB = fileSizeB;
    }


    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

}