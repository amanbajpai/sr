package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.utils.L;

import java.io.Serializable;

public class Answer extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    private Integer QuestionId;
    private Integer TaskId;
    private String Answer;

    private String Value;
    private Integer Routing;

    private transient byte[] imageByteArray;
    private transient Boolean Checked = false;

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
            result.setChecked(c.getInt(AnswerDbSchema.Query.CHECKED) == 1 ? true : false);
            result.setImageByteArray(c.getBlob(AnswerDbSchema.Query.IMAGE_BYTE_ARRAY));
        }

        L.d("Answer", result.toString());
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


    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

}