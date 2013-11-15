package com.matrix.db.entity;

import android.database.Cursor;
import com.matrix.db.TaskDbSchema;

public class Task extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Long SurveyId;
    private Long UserId;
    private String Name;
    private String Description;
    private Float Longitude;
    private Float Latitude;
    private String Language;
    private String Price;
    private String Address;


    transient private Float Distance;

    public Task() {
    }

    public Task(String name, String description) {
        this.Name = name;
        this.Description = description;
    }

    public static Task fromCursor(Cursor c) {
        Task result = new Task();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(TaskDbSchema.Query._ID));
            result.setId(c.getLong(TaskDbSchema.Query.ID));
            result.setSurveyId(c.getLong(TaskDbSchema.Query.SURVEY_ID));
            result.setUserId(c.getLong(TaskDbSchema.Query.USER_ID));
            result.setName(c.getString(TaskDbSchema.Query.NAME));
            result.setDescription(c.getString(TaskDbSchema.Query.DESCRIPTION));
            result.setLongitude(c.getFloat(TaskDbSchema.Query.LONGITUDE));
            result.setLatitude(c.getFloat(TaskDbSchema.Query.LATITUDE));
            result.setLanguage(c.getString(TaskDbSchema.Query.LANGUAGE));
            result.setPrice(c.getString(TaskDbSchema.Query.PRICE));
            result.setAddress(c.getString(TaskDbSchema.Query.ADDRESS));
            result.setDistance(c.getFloat(TaskDbSchema.Query.DISTANCE));
        }
        return result;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public Long getSurveyId() {
        return SurveyId;
    }

    public void setSurveyId(Long surveyId) {
        SurveyId = surveyId;
    }

    public Float getLongitude() {
        return Longitude;
    }

    public void setLongitude(Float longitude) {
        Longitude = longitude;
    }

    public Float getLatitude() {
        return Latitude;
    }

    public void setLatitude(Float latitude) {
        Latitude = latitude;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getPrice() {
        return Price == null ? "0" : Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }


    public Float getDistance() {
        return Distance == null ? 0 : Distance;
    }

    public void setDistance(Float distance) {
        Distance = distance;
    }
}
