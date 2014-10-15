package com.ros.smartrocket.db.entity;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.ros.smartrocket.db.TaskDbSchema;

/**
 * Data model of Task entity
 */
public class Task extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    public enum TaskStatusId {
        NONE(0), CLAIMED(1), STARTED(2), VALIDATION(3), RE_DO_TASK(4), PENDING(5), VALIDATED(6), COMPLETED(7),
        SCHEDULED(8), REJECTED(9), IN_PAYMENT_PROCESS(11), PAID(12);

        private int statusId;

        private TaskStatusId(int statusId) {
            this.statusId = statusId;
        }

        public int getStatusId() {
            return statusId;
        }
    }

    private Integer WaveId;

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
    private Double ExperienceOffer;
    private Long LongExpireTimeoutForClaimedTask;
    private Long LongPreClaimedTaskExpireAfterStart;
    private String Claimed;
    private String RedoDate;
    private String ApprovedAt;
    private String RejectedAt;
    private String SubmittedAt;

    private Integer PhotoQuestionsCount;
    private Integer NoPhotoQuestionsCount;

    private String CurrencySign;
    private String LocationName;

    private String Icon;

    private transient Boolean IsMy = false;
    private transient Boolean IsHide = false;
    private transient Long LongEndDateTime;
    private transient Long LongRedoDateTime;
    private transient Long LongStartDateTime;
    private transient Long LongClaimDateTime;
    private transient Boolean StartedStatusSent = false;
    private transient String CountryName;

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
            result.setWaveId(c.getInt(TaskDbSchema.Query.All.WAVE_ID));
            result.setUserId(c.getLong(TaskDbSchema.Query.All.USER_ID));
            result.setName(c.getString(TaskDbSchema.Query.All.NAME));
            result.setDescription(c.getString(TaskDbSchema.Query.All.DESCRIPTION));

            boolean longitudeIsNull = c.isNull(TaskDbSchema.Query.All.LONGITUDE);
            if (!longitudeIsNull) {
                result.setLongitude(c.getDouble(TaskDbSchema.Query.All.LONGITUDE));
            }

            boolean latitudeIsNull = c.isNull(TaskDbSchema.Query.All.LATITUDE);
            if (!latitudeIsNull) {
                result.setLatitude(c.getDouble(TaskDbSchema.Query.All.LATITUDE));
            }

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

            result.setIsMy(c.getInt(TaskDbSchema.Query.All.IS_MY) == 1);
            result.setIsHide(c.getInt(TaskDbSchema.Query.All.IS_HIDE) == 1);
            result.setLongEndDateTime(c.getLong(TaskDbSchema.Query.All.LONG_END_DATE_TIME));
            result.setStartedStatusSent(c.getInt(TaskDbSchema.Query.All.STARTED_STATUS_SENT) == 1);

            result.setExperienceOffer(c.getDouble(TaskDbSchema.Query.All.EXPERIENCE_OFFER));

            result.setLongExpireTimeoutForClaimedTask(c.getLong(TaskDbSchema.Query.All
                    .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setLongPreClaimedTaskExpireAfterStart(c.getLong(TaskDbSchema.Query.All
                    .LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START));
            result.setClaimed(c.getString(TaskDbSchema.Query.All.CLAIMED));
            result.setRedoDate(c.getString(TaskDbSchema.Query.All.REDO_DATE));
            result.setApprovedAt(c.getString(TaskDbSchema.Query.All.APPROVED_AT));
            result.setRejectedAt(c.getString(TaskDbSchema.Query.All.REJECTED_AT));
            result.setSubmittedAt(c.getString(TaskDbSchema.Query.All.SUBMITTED_AT));
            result.setCountryName(c.getString(TaskDbSchema.Query.All.COUNTRY_NAME));

            result.setLongRedoDateTime(c.getLong(TaskDbSchema.Query.All.LONG_REDO_DATE_TIME));
            result.setLongClaimDateTime(c.getLong(TaskDbSchema.Query.All.LONG_CLAIM_DATE_TIME));

            result.setPhotoQuestionsCount(c.getInt(TaskDbSchema.Query.All.PHOTO_QUESTIONS_COUNT));
            result.setNoPhotoQuestionsCount(c.getInt(TaskDbSchema.Query.All.NO_PHOTO_QUESTIONS_COUNT));
            result.setCurrencySign(c.getString(TaskDbSchema.Query.All.CURRENCY_SIGN));
            result.setLocationName(c.getString(TaskDbSchema.Query.All.LOCATION_NAME));

            result.setIcon(c.getString(TaskDbSchema.Query.All.ICON));
            result.setLongStartDateTime(c.getLong(TaskDbSchema.Query.All.LONG_START_DATE_TIME));
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

    public Integer getWaveId() {
        return WaveId;
    }

    public void setWaveId(Integer waveId) {
        WaveId = waveId;
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

    public Double getExperienceOffer() {
        return ExperienceOffer;
    }

    public void setExperienceOffer(Double experienceOffer) {
        ExperienceOffer = experienceOffer;
    }

    public Long getLongEndDateTime() {
        return LongEndDateTime;
    }

    public void setLongEndDateTime(Long longEndDateTime) {
        LongEndDateTime = longEndDateTime;
    }

    public Long getLongExpireTimeoutForClaimedTask() {
        return LongExpireTimeoutForClaimedTask;
    }

    public void setLongExpireTimeoutForClaimedTask(Long expireTimeoutForClaimedTask) {
        LongExpireTimeoutForClaimedTask = expireTimeoutForClaimedTask;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }


    public String getClaimed() {
        return Claimed;
    }

    public void setClaimed(String claimed) {
        Claimed = claimed;
    }

    public String getRedoDate() {
        return RedoDate;
    }

    public void setRedoDate(String redoDate) {
        RedoDate = redoDate;
    }

    public Boolean getStartedStatusSent() {
        return StartedStatusSent;
    }

    public void setStartedStatusSent(Boolean startedStatusSent) {
        StartedStatusSent = startedStatusSent;
    }

    public Long getLongRedoDateTime() {
        return LongRedoDateTime;
    }

    public void setLongRedoDateTime(Long longRedoDateTime) {
        LongRedoDateTime = longRedoDateTime;
    }

    public Long getLongClaimDateTime() {
        return LongClaimDateTime;
    }

    public void setLongClaimDateTime(Long longClaimDateTime) {
        LongClaimDateTime = longClaimDateTime;
    }

    public Integer getNoPhotoQuestionsCount() {
        return NoPhotoQuestionsCount;
    }

    public void setNoPhotoQuestionsCount(Integer noPhotoQuestionsCount) {
        NoPhotoQuestionsCount = noPhotoQuestionsCount;
    }

    public Integer getPhotoQuestionsCount() {
        return PhotoQuestionsCount;
    }

    public void setPhotoQuestionsCount(Integer photoQuestionsCount) {
        PhotoQuestionsCount = photoQuestionsCount;
    }

    public String getCurrencySign() {
        return CurrencySign;
    }

    public void setCurrencySign(String currencySign) {
        CurrencySign = currencySign;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getApprovedAt() {
        return ApprovedAt;
    }

    public void setApprovedAt(String approvedAt) {
        ApprovedAt = approvedAt;
    }

    public String getRejectedAt() {
        return RejectedAt;
    }

    public void setRejectedAt(String rejectedAt) {
        RejectedAt = rejectedAt;
    }

    public String getSubmittedAt() {
        return SubmittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        SubmittedAt = submittedAt;
    }

    public Long getLongStartDateTime() {
        return LongStartDateTime;
    }

    public void setLongStartDateTime(Long longStartDateTime) {
        LongStartDateTime = longStartDateTime;
    }

    public Long getLongPreClaimedTaskExpireAfterStart() {
        return LongPreClaimedTaskExpireAfterStart;
    }

    public void setLongPreClaimedTaskExpireAfterStart(Long longPreClaimedTaskExpireAfterStart) {
        LongPreClaimedTaskExpireAfterStart = longPreClaimedTaskExpireAfterStart;
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
        return "Task{ WaveId=" + WaveId + ", UserId='" + UserId + '\'' + ", Name='" + Name + '\''
                + ", Description='" + Description + '\'' + ", Language=" + Language
                + ", Latitude='" + Latitude + '\'' + ", Longitude='" + Longitude + '\''
                + ", Price='" + Price + '\'' + ", Address=" + Address + '}';
    }
}
