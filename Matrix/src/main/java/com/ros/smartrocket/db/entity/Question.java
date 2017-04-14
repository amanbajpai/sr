package com.ros.smartrocket.db.entity;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.QuestionDbSchema;

import java.io.Serializable;

public class Question extends BaseEntity implements Serializable, Comparable<Question> {
    @SkipFieldInContentValues
    private static final long serialVersionUID = -4706526633427191907L;

    @SkipFieldInContentValues
    public static final int ACTION_TICK = 0;
    @SkipFieldInContentValues
    public static final int ACTION_CROSS = 1;
    @SkipFieldInContentValues
    public static final int ACTION_BOTH = 2;
    @SkipFieldInContentValues
    public static final int ACTION_NOTHING = 3;

    public String getSubQuestionNumber() {
        return subQuestionNumber;
    }

    public void setSubQuestionNumber(String subQuestionNumber) {
        this.subQuestionNumber = subQuestionNumber;
    }

    @Override
    public int compareTo(Question another) {
        if (this.orderId > another.orderId) {
            return 1;
        }
        return this.orderId < another.orderId ? -1 : 0;
    }

    public boolean isRedo() {
        return isRedo;
    }

    public void setRedo(boolean redo) {
        isRedo = redo;
    }

    @Override
    public String toString() {
        return "Question{" +
                "waveId=" + waveId +
                ", taskId=" + taskId +
                ", type=" + type +
                ", orderId=" + orderId +
                ", routing=" + routing +
                ", isRedo=" + isRedo +
                '}';
    }


    public enum QuestionType {
        NONE(0), MULTIPLE_CHOICE(1), PHOTO(2), VALIDATION(3), REJECT(4), OPEN_COMMENT(5), SINGLE_CHOICE(6),
        VIDEO(7), NUMBER(8), INSTRUCTION(9), MASS_AUDIT(10), MAIN_SUB_QUESTION(11);

        private int typeId;

        QuestionType(int typeId) {
            this.typeId = typeId;
        }

        public int getTypeId() {
            return typeId;
        }
    }

    @SerializedName("MissionId")
    private Integer missionId;
    @SerializedName("ProductId")
    private Integer productId;
    @SerializedName("WaveId")
    private Integer waveId;
    @SerializedName("TaskId")
    private Integer taskId;
    @SerializedName("QuestionFormatted")
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
    @SerializedName("ValidationCommentFormatted")
    private String validationComment;
    @SerializedName("PresetValidationText")
    private String presetValidationText;
    private transient Integer previousQuestionOrderId;
    private transient Integer nextAnsweredQuestionId;

    // [START Mass Audit]
    @SkipFieldInContentValues
    @SerializedName("Categories")
    private Category[] categoriesArray;
    private String categories = "";
    @SerializedName("Action")
    private Integer action;
    @SerializedName("ParentQuestionId")
    private Integer parentQuestionId;
    @SerializedName("IsRequired")
    private Boolean isRequired;

    @SkipFieldInContentValues
    @SerializedName("Children")
    private Question[] childrenQuestions;
    @SkipFieldInContentValues
    private transient String subQuestionNumber;

    @SerializedName("IsRedo")
    private Boolean isRedo;
    // [END Mass Audit]

    @SkipFieldInContentValues
    @SerializedName("AskIf")
    private AskIf[] askIfArray;
    private String askIf = "";

    private String taskLocation = "";
    @SkipFieldInContentValues
    @SerializedName("TaskLocation")
    private TaskLocation taskLocationObject;

    @SkipFieldInContentValues
    @SerializedName("Answers")
    private Answer[] answers;

    private transient String instructionFileUri;

    public Question() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public static Question fromCursor(Cursor c) {
        Question result = new Question();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(QuestionDbSchema.Query._ID));
            result.setId(c.getInt(QuestionDbSchema.Query.ID));
            result.setWaveId(c.getInt(QuestionDbSchema.Query.WAVE_ID));
            result.setTaskId(c.getInt(QuestionDbSchema.Query.TASK_ID));
            result.setMissionId(c.getInt(QuestionDbSchema.Query.MISSION_ID));
            result.setQuestion(c.getString(QuestionDbSchema.Query.QUESTION));
            result.setType(c.getInt(QuestionDbSchema.Query.TYPE));
            result.setOrderId(c.getInt(QuestionDbSchema.Query.ORDER_ID));
            result.setMaximumCharacters(c.getInt(QuestionDbSchema.Query.MAXIMUM_CHARACTERS));
            result.setMaximumPhotos(c.getInt(QuestionDbSchema.Query.MAXIMUM_PHOTOS));
            result.setShowBackButton(c.getInt(QuestionDbSchema.Query.SHOW_BACK_BUTTON) == 1);
            result.setAllowMultiplyPhotos(c.getInt(QuestionDbSchema.Query.ALLOW_MULTIPLY_PHOTOS) == 1);
            result.setAskIf(c.getString(QuestionDbSchema.Query.ASK_IF));
            result.setTaskLocation(c.getString(QuestionDbSchema.Query.TASK_LOCATION));
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
            result.setInstructionFileUri(c.getString(QuestionDbSchema.Query.INSTRUCTION_FILE_URI));

            result.setNextAnsweredQuestionId(c.getInt(QuestionDbSchema.Query.NEXT_ANSWERED_QUESTION_ID));

            result.setAskIfArray(AskIf.getAskIfArray(result.getAskIf()));
            result.setTaskLocationObject(TaskLocation.getTaskLocation(result.getTaskLocation()));

            result.setParentQuestionId(c.getInt(QuestionDbSchema.Query.PARENT_QUESTION_ID));
            result.setCategories(c.getString(QuestionDbSchema.Query.CATEGORIES));
            result.setCategoriesArray(Category.getCategoryArray(result.getCategories()));
            result.setAction(c.getInt(QuestionDbSchema.Query.ACTION));
            result.setRequired(c.getInt(QuestionDbSchema.Query.IS_REQUIRED) == 1);
            result.setProductId(c.getInt(QuestionDbSchema.Query.PRODUCT_ID));
            result.setRedo(c.getInt(QuestionDbSchema.Query.IS_REDO) == 1);
        }

        return result;
    }

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
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

    public AskIf[] getAskIfArray() {
        return askIfArray;
    }

    public void setAskIfArray(AskIf[] askIfArray) {
        this.askIfArray = askIfArray;
    }

    public TaskLocation getTaskLocationObject() {
        return taskLocationObject;
    }

    public void setTaskLocationObject(TaskLocation taskLocationObject) {
        this.taskLocationObject = taskLocationObject;
    }

    public String getTaskLocation() {
        return taskLocation;
    }

    public void setTaskLocation(String taskLocation) {
        this.taskLocation = taskLocation;
    }

    public String getInstructionFileUri() {
        return instructionFileUri;
    }

    public void setInstructionFileUri(String instructionFileUri) {
        this.instructionFileUri = instructionFileUri;
    }

    public Integer getNextAnsweredQuestionId() {
        return nextAnsweredQuestionId;
    }

    public void setNextAnsweredQuestionId(Integer nextAnsweredQuestionId) {
        this.nextAnsweredQuestionId = nextAnsweredQuestionId;
    }

    public Integer getParentQuestionId() {
        return parentQuestionId;
    }

    public void setParentQuestionId(Integer parentQuestionId) {
        this.parentQuestionId = parentQuestionId;
    }

    public Category[] getCategoriesArray() {
        return categoriesArray;
    }

    public void setCategoriesArray(Category[] categoriesArray) {
        this.categoriesArray = categoriesArray;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getCategories() {
        return categories;
    }

    public Question[] getChildQuestions() {
        return childrenQuestions;
    }

    public void setChildQuestions(Question[] childrenQuestions) {
        this.childrenQuestions = childrenQuestions;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Boolean isRequired() {
        return isRequired;
    }

    public void setRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }


}