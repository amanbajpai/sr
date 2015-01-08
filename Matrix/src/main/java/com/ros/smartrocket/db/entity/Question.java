package com.ros.smartrocket.db.entity;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.utils.L;

import java.io.Serializable;

public class Question extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    public enum QuestionType {
        NONE(0), MULTIPLE_CHOICE(1), PHOTO(2), VALIDATION(3), REJECT(4), OPEN_COMMENT(5), SINGLE_CHOICE(6),
        VIDEO(7), NUMBER(8), INSTRUCTION(9);

        private int typeId;

        private QuestionType(int typeId) {
            this.typeId = typeId;
        }

        public int getTypeId() {
            return typeId;
        }
    }

    @SerializedName("WaveId")
    private Integer waveId;
    @SerializedName("TaskId")
    private Integer taskId;
    @SerializedName("Question")
    private String question = "";
    @SerializedName("Type")
    private Integer type;
    @SerializedName("OrderId")
    private Integer orderId;
    @SerializedName("MaximumCharacters")
    private Integer maximumCharacters;
    @SerializedName("MaximumPhotos")
    private Integer maximumPhotos;
    @SerializedName("ShowBackButton")
    private Boolean showBackButton;
    @SerializedName("AllowMultiplyPhotos")
    private Boolean allowMultiplyPhotos;
    @SerializedName("AskIf")
    private String askIf = "";
    @SerializedName("MinValue")
    private Integer minValue;
    @SerializedName("MaxValue")
    private Integer maxValue;
    @SerializedName("PatternType")
    private Integer patternType;
    @SerializedName("VideoSource")
    private Integer videoSource;
    @SerializedName("PhotoSource")
    private Integer photoSource;
    @SerializedName("VideoUrl")
    private String videoUrl;
    @SerializedName("PhotoUrl")
    private String photoUrl;

    @SerializedName("Routing")
    private Integer routing;

    @SerializedName("ValidationComment")
    private String validationComment;
    @SerializedName("PresetValidationText")
    private String presetValidationText;

    private transient Integer previousQuestionOrderId;

    @SkipFieldInContentValues
    @SerializedName("Answers")
    private Answer[] answers;

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
            result.setPresetValidationText(c.getString(QuestionDbSchema.Query.PRESENT_VALIDATION_TEXT));

            result.setMinValue(c.getInt(QuestionDbSchema.Query.MIN_VALUES));
            result.setMaxValue(c.getInt(QuestionDbSchema.Query.MAX_VALUES));
            result.setPatternType(c.getInt(QuestionDbSchema.Query.PATTERN_TYPE));
            result.setVideoSource(c.getInt(QuestionDbSchema.Query.VIDEO_SOURCE));
            result.setPhotoSource(c.getInt(QuestionDbSchema.Query.PHOTO_SOURCE));
            result.setVideoUrl(c.getString(QuestionDbSchema.Query.VIDEO_URL));
            result.setPhotoUrl(c.getString(QuestionDbSchema.Query.PHOTO_URL));

            result.setRouting(c.getInt(QuestionDbSchema.Query.ROUTING));
        }

        L.d("question", result.toString());
        return result;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getWaveId() {
        return waveId;
    }

    public void setWaveId(Integer waveId) {
        this.waveId = waveId;
    }

    public Answer[] getAnswers() {
        return answers;
    }

    public void setAnswers(Answer[] answers) {
        this.answers = answers;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getMaximumCharacters() {
        return maximumCharacters;
    }

    public void setMaximumCharacters(Integer maximumCharacters) {
        this.maximumCharacters = maximumCharacters;
    }

    public Integer getMaximumPhotos() {
        return maximumPhotos;
    }

    public void setMaximumPhotos(Integer maximumPhotos) {
        this.maximumPhotos = maximumPhotos;
    }

    public Boolean getShowBackButton() {
        return showBackButton;
    }

    public void setShowBackButton(Boolean showBackButton) {
        this.showBackButton = showBackButton;
    }

    public Boolean getAllowMultiplyPhotos() {
        return allowMultiplyPhotos;
    }

    public void setAllowMultiplyPhotos(Boolean allowMultiplyPhotos) {
        this.allowMultiplyPhotos = allowMultiplyPhotos;
    }

    public String getAskIf() {
        return askIf;
    }

    public void setAskIf(String askIf) {
        this.askIf = askIf;
    }


    public Integer getPreviousQuestionOrderId() {
        return previousQuestionOrderId;
    }

    public void setPreviousQuestionOrderId(Integer previousQuestionOrderId) {
        this.previousQuestionOrderId = previousQuestionOrderId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getValidationComment() {
        return validationComment;
    }

    public void setValidationComment(String validationComment) {
        this.validationComment = validationComment;
    }


    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getPatternType() {
        return patternType;
    }

    public void setPatternType(Integer patternType) {
        this.patternType = patternType;
    }


    public Integer getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(Integer videoSource) {
        this.videoSource = videoSource;
    }

    public Integer getPhotoSource() {
        return photoSource;
    }

    public void setPhotoSource(Integer photoSource) {
        this.photoSource = photoSource;
    }

    public Integer getRouting() {
        return routing;
    }

    public void setRouting(Integer routing) {
        this.routing = routing;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPresetValidationText() {
        return presetValidationText;
    }

    public void setPresetValidationText(String presetValidationText) {
        this.presetValidationText = presetValidationText;
    }

}