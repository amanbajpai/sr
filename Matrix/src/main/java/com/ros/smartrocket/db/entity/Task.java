package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.google.android.gms.maps.model.LatLng;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.utils.L;

/**
 * Data model of Task entity
 */
public class Task extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    public enum TaskStatusId {
        none(0), claimed(1), started(2), validation(3), reDoTask(4), pending(5), completed(6), scheduled(7);

        private int statusId;

        private TaskStatusId(int statusId) {
            this.statusId = statusId;
        }

        public int getStatusId() {
            return statusId;
        }
    }

    private Integer SurveyId;

    private Long UserId;
    private String Name = "";
    private String Description;
    private Double Longitude;
    private Double Latitude;
    private String Language;
    private Double Price;
    private String Address;
    private Float Distance;

    private String RemakeTill;
    private String Started;
    private Integer StatusId;
    private String Status;
    private String StartDateTime;
    private String EndDateTime;

    private transient Boolean IsMy = false;
    private transient Boolean IsHide = false;

    public Task() {
    }

    public Task(String name, String description) {
        this.Name = name;

        this.Description = description;
    }

    public static Task fromCursor(Cursor c) {
        Task result = new Task();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(TaskDbSchema.Query.All._ID));
            result.setId(c.getInt(TaskDbSchema.Query.All.ID));
            result.setSurveyId(c.getInt(TaskDbSchema.Query.All.SURVEY_ID));
            result.setUserId(c.getLong(TaskDbSchema.Query.All.USER_ID));
            result.setName(c.getString(TaskDbSchema.Query.All.NAME));
            result.setDescription(c.getString(TaskDbSchema.Query.All.DESCRIPTION));
            result.setLongitude(c.getDouble(TaskDbSchema.Query.All.LONGITUDE));
            result.setLatitude(c.getDouble(TaskDbSchema.Query.All.LATITUDE));
            result.setLanguage(c.getString(TaskDbSchema.Query.All.LANGUAGE));
            result.setPrice(c.getDouble(TaskDbSchema.Query.All.PRICE));
            result.setAddress(c.getString(TaskDbSchema.Query.All.ADDRESS));
            result.setDistance(c.getFloat(TaskDbSchema.Query.All.DISTANCE));

            result.setRemakeTill(c.getString(TaskDbSchema.Query.All.REMAKE_TILL));
            result.setStarted(c.getString(TaskDbSchema.Query.All.STARTED));
            result.setStatusId(c.getInt(TaskDbSchema.Query.All.STATUS_ID));
            result.setStatus(c.getString(TaskDbSchema.Query.All.STATUS));
            result.setStartDateTime(c.getString(TaskDbSchema.Query.All.START_DATE_TIME));
            result.setEndDateTime(c.getString(TaskDbSchema.Query.All.END_DATE_TIME));

            result.setIsMy(c.getInt(TaskDbSchema.Query.All.IS_MY) == 0 ? false : true);
            result.setIsHide(c.getInt(TaskDbSchema.Query.All.IS_HIDE) == 0 ? false : true);
        }
        L.d("Task", result.toString());
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

    public Integer getSurveyId() {
        return SurveyId;
    }

    public void setSurveyId(Integer surveyId) {
        SurveyId = surveyId;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public Double getPrice() {
        return Price == null ? 0 : Price;
    }

    public void setPrice(Double price) {
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

    public Boolean getIsMy() {
        return IsMy;
    }

    public void setIsMy(Boolean isMy) {
        IsMy = isMy;
    }


    public String getRemakeTill() {
        return RemakeTill;
    }

    public void setRemakeTill(String remakeTill) {
        RemakeTill = remakeTill;
    }

    public String getStarted() {
        return Started;
    }

    public void setStarted(String started) {
        Started = started;
    }

    public Integer getStatusId() {
        return StatusId;
    }

    public void setStatusId(Integer statusId) {
        StatusId = statusId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        StartDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    public Boolean getIsHide() {
        return IsHide;
    }

    public void setIsHide(Boolean isHide) {
        IsHide = isHide;
    }

    /**
     * Return {@link com.google.android.gms.maps.model.LatLng} object with {@link Task}
     * coordinates
     *
     * @return
     */
    public LatLng getLatLng() {
        return new LatLng(this.Latitude, this.Longitude);
    }

    @Override
    public String toString() {
        return "Task{ SurveyId=" + SurveyId + ", UserId='" + UserId + '\'' + ", Name='" + Name + '\'' + ", Description='" + Description + '\'' + ", Language=" + Language + ", Latitude='" + Latitude + '\'' + ", Longitude='" + Longitude + '\'' + ", Price='" + Price + '\'' + ", Address=" + Address + '}';
    }
}
