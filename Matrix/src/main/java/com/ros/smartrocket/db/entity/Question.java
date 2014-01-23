package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.utils.L;

import java.io.Serializable;

public class Question extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    private Integer SurveyId;
    private Integer TaskId;
    private String Question = "";
    private Integer Type;
    private Integer OrderId;
    private Integer MaximumCharacters;
    private Integer MaximumPhotos;
    private Boolean ShowBackButton;
    private Boolean AllowMultiplyPhotos;
    private String AskIf = "";

    private transient Integer PreviousQuestionOrderId;

    @SkipFieldInContentValues
    private Answer[] Answers;

    public Question() {
    }

    public static Question fromCursor(Cursor c) {
        Question result = new Question();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(QuestionDbSchema.Query._ID));
            result.setId(c.getInt(QuestionDbSchema.Query.ID));
            result.setSurveyId(c.getInt(QuestionDbSchema.Query.SURVEY_ID));
            result.setTaskId(c.getInt(QuestionDbSchema.Query.TASK_ID));
            result.setQuestion(c.getString(QuestionDbSchema.Query.QUESTION));
            result.setType(c.getInt(QuestionDbSchema.Query.TYPE));
            result.setOrderId(c.getInt(QuestionDbSchema.Query.ORDER_ID));
            result.setMaximumCharacters(c.getInt(QuestionDbSchema.Query.MAXIMUM_CHARACTERS));
            result.setMaximumPhotos(c.getInt(QuestionDbSchema.Query.MAXIMUM_PHOTOS));
            result.setShowBackButton(c.getInt(QuestionDbSchema.Query.SHOW_BACK_BUTTON) == 1 ? true : false);
            result.setAllowMultiplyPhotos(c.getInt(QuestionDbSchema.Query.ALLOW_MULTIPLY_PHOTOS) == 1 ? true : false);
            result.setAskIf(c.getString(QuestionDbSchema.Query.ASK_IF));
            result.setPreviousQuestionOrderId(c.getInt(QuestionDbSchema.Query.PREVIOUS_QUESTION_ORDER_ID));
        }

        L.d("Question", result.toString());
        return result;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
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


    public Integer getOrderId() {
        return OrderId;
    }

    public void setOrderId(Integer orderId) {
        OrderId = orderId;
    }

    public Integer getMaximumCharacters() {
        return MaximumCharacters;
    }

    public void setMaximumCharacters(Integer maximumCharacters) {
        MaximumCharacters = maximumCharacters;
    }

    public Integer getMaximumPhotos() {
        return MaximumPhotos;
    }

    public void setMaximumPhotos(Integer maximumPhotos) {
        MaximumPhotos = maximumPhotos;
    }

    public Boolean getShowBackButton() {
        return ShowBackButton;
    }

    public void setShowBackButton(Boolean showBackButton) {
        ShowBackButton = showBackButton;
    }

    public Boolean getAllowMultiplyPhotos() {
        return AllowMultiplyPhotos;
    }

    public void setAllowMultiplyPhotos(Boolean allowMultiplyPhotos) {
        AllowMultiplyPhotos = allowMultiplyPhotos;
    }

    public String getAskIf() {
        return AskIf;
    }

    public void setAskIf(String askIf) {
        AskIf = askIf;
    }


    public Integer getPreviousQuestionOrderId() {
        return PreviousQuestionOrderId;
    }

    public void setPreviousQuestionOrderId(Integer previousQuestionOrderId) {
        PreviousQuestionOrderId = previousQuestionOrderId;
    }

    public Integer getTaskId() {
        return TaskId;
    }

    public void setTaskId(Integer taskId) {
        TaskId = taskId;
    }

}