package com.matrix.db.entity;

import android.database.Cursor;
import com.matrix.db.SurveyDbSchema;

public class Survey extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Long UserId;
    private String Name;
    private String Description;
    private Float Longitude;
    private Float Latitude;
    private String Language;


    public Survey() {
    }

    public Survey(String name, String description) {
        this.Name = name;
        this.Description = description;
    }

    public static Survey fromCursor(Cursor c) {
        Survey result = new Survey();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(SurveyDbSchema.Query._ID));
            result.setId(c.getLong(SurveyDbSchema.Query.ID));
            result.setUserId(c.getLong(SurveyDbSchema.Query.USER_ID));
            result.setName(c.getString(SurveyDbSchema.Query.NAME));
            result.setDescription(c.getString(SurveyDbSchema.Query.DESCRIPTION));
            result.setLongitude(c.getFloat(SurveyDbSchema.Query.LONGITUDE));
            result.setLatitude(c.getFloat(SurveyDbSchema.Query.LATITUDE));
            result.setLanguage(c.getString(SurveyDbSchema.Query.LANGUAGE));
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
}
