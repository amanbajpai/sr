package com.ros.smartrocket.db.entity.question;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.CustomFieldImageUrlDbSchema;
import com.ros.smartrocket.db.entity.BaseEntity;

import java.io.Serializable;

public class CustomFieldImageUrls extends BaseEntity implements Serializable {
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
    @SerializedName("Image_Url")
    public String imageUrl;

    public CustomFieldImageUrls() {
    }

    public static CustomFieldImageUrls fromCursor(Cursor c) {
        CustomFieldImageUrls result = new CustomFieldImageUrls();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(CustomFieldImageUrlDbSchema.Query._ID));
            result.setId(c.getInt(CustomFieldImageUrlDbSchema.Query.ID));
            result.setQuestionId(c.getInt(CustomFieldImageUrlDbSchema.Query.QUESTION_ID));
            result.setTaskId(c.getInt(CustomFieldImageUrlDbSchema.Query.TASK_ID));
            result.setMissionId(c.getInt(CustomFieldImageUrlDbSchema.Query.MISSION_ID));
            result.setProductId(c.getInt(CustomFieldImageUrlDbSchema.Query.PRODUCT_ID));
            result.setImageUrl(c.getString(CustomFieldImageUrlDbSchema.Query.IMAGE_URL));
        }

        return result;
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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}