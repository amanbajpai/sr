package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.utils.L;

import java.io.Serializable;

public class Answer extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    private Integer QuestionId;
    private String Text;
    private boolean Checked = false;
    private byte[] imageByteArray;

    public Answer() {
    }

    public static Answer fromCursor(Cursor c) {
        Answer result = new Answer();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(AnswerDbSchema.Query._ID));
            result.setId(c.getInt(AnswerDbSchema.Query.ID));
            result.setQuestionId(c.getInt(AnswerDbSchema.Query.QUESTION_ID));
            result.setText(c.getString(AnswerDbSchema.Query.TEXT));
            result.setImageByteArray(c.getBlob(AnswerDbSchema.Query.IMAGE_BYTE_ARRAY));
        }

        L.d("Answer", result.toString());
        return result;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
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