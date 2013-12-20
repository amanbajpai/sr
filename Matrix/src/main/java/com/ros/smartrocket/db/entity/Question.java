package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.utils.L;

import java.io.Serializable;

public class Question extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    private Integer SurveyId;
    private String Description = "";
    private Integer Type;
    private boolean Checked = false;
    private Integer NextQuestionId;
    private Integer PreviousQuestionId;

    private Answer[] Answers;

    public Question() {
    }

    public static Question fromCursor(Cursor c) {
        Question result = new Question();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(QuestionDbSchema.Query._ID));
            result.setId(c.getInt(QuestionDbSchema.Query.ID));
            result.setSurveyId(c.getInt(QuestionDbSchema.Query.SURVEY_ID));
            result.setDescription(c.getString(QuestionDbSchema.Query.DESCRIPTION));
            result.setType(c.getInt(QuestionDbSchema.Query.TYPE));
            result.setPreviousQuestionId(c.getInt(QuestionDbSchema.Query.PREVIOUS_QUESTION_ID));
            result.setNextQuestionId(c.getInt(QuestionDbSchema.Query.NEXT_QUESTION_ID));
        }

        L.d("Question", result.toString());
        return result;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isChecked() {
        return Checked;
    }

    public void setChecked(boolean checked) {
        Checked = checked;
    }

    public Integer getSurveyId() {
        return SurveyId;
    }

    public void setSurveyId(Integer surveyId) {
        SurveyId = surveyId;
    }

    public Answer[] getAnswers() {
        return Answers;
    }

    public void setAnswers(Answer[] answers) {
        Answers = answers;
    }


    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }


    public Integer getNextQuestionId() {
        return NextQuestionId;
    }

    public void setNextQuestionId(Integer nextQuestionId) {
        NextQuestionId = nextQuestionId;
    }

    public Integer getPreviousQuestionId() {
        return PreviousQuestionId;
    }

    public void setPreviousQuestionId(Integer previousQuestionId) {
        PreviousQuestionId = previousQuestionId;
    }


    public void toggleChecked() {
        Checked = !Checked;
    }
}