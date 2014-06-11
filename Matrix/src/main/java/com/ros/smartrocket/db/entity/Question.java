package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.utils.L;

import java.io.Serializable;

public class Question extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    public enum QuestionType {
        none(0), multiple_choice(1), photo(2), validation(3), reject(4), openComment(5), single_choice(6),
        video(7), number(8);

        private int typeId;

        private QuestionType(int typeId) {
            this.typeId = typeId;
        }

        public int getTypeId() {
            return typeId;
        }
    }

    private Integer WaveId;
    private Integer TaskId;
    private String Question = "";
    private Integer Type;
    private Integer OrderId;
    private Integer MaximumCharacters;
    private Integer MaximumPhotos;
    private Boolean ShowBackButton;
    private Boolean AllowMultiplyPhotos;
    private String AskIf = "";
    private Integer MinValue;
    private Integer MaxValue;
    private Integer PatternType;
    private Integer VideoSource;
    private Integer PhotoSource;

    private String ValidationComment;

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
            result.setWaveId(c.getInt(QuestionDbSchema.Query.WAVE_ID));
            result.setTaskId(c.getInt(QuestionDbSchema.Query.TASK_ID));
            result.setQuestion(c.getString(QuestionDbSchema.Query.QUESTION));
            result.setType(c.getInt(QuestionDbSchema.Query.TYPE));
            result.setOrderId(c.getInt(QuestionDbSchema.Query.ORDER_ID));
            result.setMaximumCharacters(c.getInt(QuestionDbSchema.Query.MAXIMUM_CHARACTERS));
            result.setMaximumPhotos(c.getInt(QuestionDbSchema.Query.MAXIMUM_PHOTOS));
            result.setShowBackButton(c.getInt(QuestionDbSchema.Query.SHOW_BACK_BUTTON) == 1);
            result.setAllowMultiplyPhotos(c.getInt(QuestionDbSchema.Query.ALLOW_MULTIPLY_PHOTOS) == 1);
            result.setAskIf(c.getString(QuestionDbSchema.Query.ASK_IF));
            result.setPreviousQuestionOrderId(c.getInt(QuestionDbSchema.Query.PREVIOUS_QUESTION_ORDER_ID));
            result.setValidationComment(c.getString(QuestionDbSchema.Query.VALIDATION_COMMENT));

            result.setMinValue(c.getInt(QuestionDbSchema.Query.MIN_VALUES));
            result.setMaxValue(c.getInt(QuestionDbSchema.Query.MAX_VALUES));
            result.setPatternType(c.getInt(QuestionDbSchema.Query.PATTERN_TYPE));
            result.setVideoSource(c.getInt(QuestionDbSchema.Query.VIDEO_SOURCE));
            result.setPhotoSource(c.getInt(QuestionDbSchema.Query.PHOTO_SOURCE));
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

    public Integer getWaveId() {
        return WaveId;
    }

    public void setWaveId(Integer waveId) {
        WaveId = waveId;
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

    public String getValidationComment() {
        return ValidationComment;
    }

    public void setValidationComment(String validationComment) {
        ValidationComment = validationComment;
    }


    public Integer getMinValue() {
        return MinValue;
    }

    public void setMinValue(Integer minValue) {
        MinValue = minValue;
    }

    public Integer getMaxValue() {
        return MaxValue;
    }

    public void setMaxValue(Integer maxValue) {
        MaxValue = maxValue;
    }

    public Integer getPatternType() {
        return PatternType;
    }

    public void setPatternType(Integer patternType) {
        PatternType = patternType;
    }


    public Integer getVideoSource() {
        return VideoSource;
    }

    public void setVideoSource(Integer videoSource) {
        VideoSource = videoSource;
    }

    public Integer getPhotoSource() {
        return PhotoSource;
    }

    public void setPhotoSource(Integer photoSource) {
        PhotoSource = photoSource;
    }

}