package com.ros.smartrocket.db.entity;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.TaskDbSchema;

/**
 * Data model of Task entity
 */
public class Task extends BaseEntity {
    @SkipFieldInContentValues
    private static final long serialVersionUID = 5410835468659163958L;

    public enum TaskStatusId {
        CAN_PRE_CLAIM(-1), NONE(0), CLAIMED(1), STARTED(2), VALIDATION(3), RE_DO_TASK(4), PENDING(5), VALIDATED(6),
        COMPLETED(7), SCHEDULED(8), REJECTED(9), IN_PAYMENT_PROCESS(11), PAID(12), WITHDRAW(14);

        private int statusId;

        private TaskStatusId(int statusId) {
            this.statusId = statusId;
        }

        public int getStatusId() {
            return statusId;
        }
    }

    @SerializedName("MissionId")
    private Integer missionId;
    @SerializedName("WaveId")
    private Integer waveId;
    @SerializedName("UserId")
    private Long userId;
    @SerializedName("Name")
    private String name = "";
    @SerializedName("Description")
    private String description;
    @SerializedName("Longitude")
    private Double longitude;
    @SerializedName("Latitude")
    private Double latitude;
    @SerializedName("Language")
    private String language;
    @SerializedName("Price")
    private Double price;
    @SerializedName("Address")
    private String address;
    @SerializedName("Distance")
    private Float distance;
    @SerializedName("RemakeTill")
    private String remakeTill;

    @SerializedName("Started")
    private String started;
    @SerializedName("StatusId")
    private Integer statusId;
    @SerializedName("Status")
    private String status;
    @SerializedName("StartDateTime")
    private String startDateTime;
    @SerializedName("EndDateTime")
    private String endDateTime;
    @SerializedName("ExpireDateTime")
    private String expireDateTime;
    @SerializedName("ExperienceOffer")
    private Double experienceOffer;
    @SerializedName("LongExpireTimeoutForClaimedTask")
    private Long longExpireTimeoutForClaimedTask;
    @SerializedName("LongPreClaimedTaskExpireAfterStart")
    private Long longPreClaimedTaskExpireAfterStart;
    @SerializedName("Claimed")
    private String claimed;
    @SerializedName("RedoDate")
    private String redoDate;
    @SerializedName("ApprovedAt")
    private String approvedAt;
    @SerializedName("RejectedAt")
    private String rejectedAt;
    @SerializedName("SubmittedAt")
    private String submittedAt;

    @SerializedName("ApproxMissionDuration")
    private Integer approxMissionDuration;

    @SerializedName("CurrencySign")
    private String currencySign;
    @SerializedName("LocationName")
    private String locationName;

    @SerializedName("Icon")
    private String icon;
    @SerializedName("FeedBackShort")
    private String feedBackShort;
    @SerializedName("FeedBackCommentFormatted")
    private String feedBackCommentFormatted;

    private transient Boolean isMy = false;
    private transient Boolean isHide = false;
    private transient Long longEndDateTime;
    private transient Long longRedoDateTime;
    private transient Long longStartDateTime;
    private transient Long longClaimDateTime;
    private transient Long longExpireDateTime;
    private transient Boolean startedStatusSent = false;
    private transient String countryName;

    private transient Double longitudeToValidation;
    private transient Double latitudeToValidation;

    public Task() {
    }

    public Task(String name, String description) {
        this.name = name;

        this.description = description;
    }

    public static Task fromCursor(Cursor c) {
        Task result = new Task();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(TaskDbSchema.Query.All._ID));
            result.setId(c.getInt(TaskDbSchema.Query.All.ID));
            result.setMissionId(c.getInt(TaskDbSchema.Query.All.MISSION_ID));
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
            result.setExpireDateTime(c.getString(TaskDbSchema.Query.All.EXPIRE_DATE_TIME));

            result.setLongEndDateTime(c.getLong(TaskDbSchema.Query.All.LONG_END_DATE_TIME));
            result.setLongRedoDateTime(c.getLong(TaskDbSchema.Query.All.LONG_REDO_DATE_TIME));
            result.setLongClaimDateTime(c.getLong(TaskDbSchema.Query.All.LONG_CLAIM_DATE_TIME));
            result.setLongStartDateTime(c.getLong(TaskDbSchema.Query.All.LONG_START_DATE_TIME));
            result.setLongExpireDateTime(c.getLong(TaskDbSchema.Query.All.LONG_EXPIRE_DATE_TIME));

            result.setIsMy(c.getInt(TaskDbSchema.Query.All.IS_MY) == 1);
            result.setIsHide(c.getInt(TaskDbSchema.Query.All.IS_HIDE) == 1);
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

            result.setCurrencySign(c.getString(TaskDbSchema.Query.All.CURRENCY_SIGN));
            result.setLocationName(c.getString(TaskDbSchema.Query.All.LOCATION_NAME));

            result.setIcon(c.getString(TaskDbSchema.Query.All.ICON));

            result.setLatitudeToValidation(c.getDouble(TaskDbSchema.Query.All.LATITUDE_TO_VALIDATION));
            result.setLongitudeToValidation(c.getDouble(TaskDbSchema.Query.All.LONGITUDE_TO_VALIDATION));

            result.setApproxMissionDuration(c.getInt(TaskDbSchema.Query.All.APPROX_MISSION_DURATION));

            result.setFeedBackShort(c.getString(TaskDbSchema.Query.All.FEEDBACK_SHORT));
            result.setFeedBackCommentFormatted(c.getString(TaskDbSchema.Query.All.FEEDBACK_COMMENT_FORMATTED));
        }
        return result;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put("countryName", countryName);
        contentValues.put("rejectedAt", rejectedAt);
        contentValues.put("startedStatusSent", startedStatusSent);
        contentValues.put("experienceOffer", experienceOffer);
        contentValues.put("userId", userId);
        contentValues.put("locationName", locationName);
        contentValues.put("missionId", missionId);
        contentValues.put("endDateTime", endDateTime);
        contentValues.put("claimed", claimed);
        contentValues.put("isHide", isHide);
        contentValues.put("submittedAt", submittedAt);
        contentValues.put("isMy", isMy);
        contentValues.put("status", status);
        contentValues.put("id", getId());
        contentValues.put("longClaimDateTime", longClaimDateTime);
        contentValues.put("longEndDateTime", longEndDateTime);
        contentValues.put("statusId", statusId);
        contentValues.put("remakeTill", remakeTill);
        contentValues.put("longitudeToValidation", longitudeToValidation);
        contentValues.put("approvedAt", approvedAt);
        contentValues.put("waveId", waveId);
        contentValues.put("started", started);
        contentValues.put("longExpireTimeoutForClaimedTask", longExpireTimeoutForClaimedTask);
        contentValues.put("currencySign", currencySign);
        contentValues.put("address", address);
        contentValues.put("icon", icon);
        contentValues.put("expireDateTime", expireDateTime);
        contentValues.put("startDateTime", startDateTime);
        contentValues.put("longitude", longitude);
        contentValues.put("longExpireDateTime", longExpireDateTime);
        contentValues.put("latitudeToValidation", latitudeToValidation);
        contentValues.put("redoDate", redoDate);
        contentValues.put("price", price);
        contentValues.put("description", description);
        contentValues.put("name", name);
        contentValues.put("longPreClaimedTaskExpireAfterStart", longPreClaimedTaskExpireAfterStart);
        contentValues.put("latitude", latitude);
        contentValues.put("deleted", isDeleted());
        contentValues.put("longRedoDateTime", longRedoDateTime);
        contentValues.put("language", language);
        contentValues.put("distance", distance);
        contentValues.put("longStartDateTime", longStartDateTime);
        contentValues.put("approxMissionDuration", approxMissionDuration);
        contentValues.put("feedBackShort", feedBackShort);
        contentValues.put("feedBackCommentFormatted", feedBackCommentFormatted);

        return contentValues;
    }

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getWaveId() {
        return waveId;
    }

    public void setWaveId(Integer waveId) {
        this.waveId = waveId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getPrice() {
        return price == null ? 0 : price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAddress() {
        return address==null?"":address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Float getDistance() {
        return distance == null ? 0 : distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Boolean getIsMy() {
        return isMy;
    }

    public void setIsMy(Boolean isMy) {
        this.isMy = isMy;
    }


    public String getRemakeTill() {
        return remakeTill;
    }

    public void setRemakeTill(String remakeTill) {
        this.remakeTill = remakeTill;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Boolean getIsHide() {
        return isHide;
    }

    public void setIsHide(Boolean isHide) {
        this.isHide = isHide;
    }

    public Double getExperienceOffer() {
        return experienceOffer == null ? 0 : experienceOffer;
    }

    public void setExperienceOffer(Double experienceOffer) {
        this.experienceOffer = experienceOffer;
    }

    public Long getLongEndDateTime() {
        return longEndDateTime;
    }

    public void setLongEndDateTime(Long longEndDateTime) {
        this.longEndDateTime = longEndDateTime;
    }

    public Long getLongExpireTimeoutForClaimedTask() {
        return longExpireTimeoutForClaimedTask == null ? 0 : longExpireTimeoutForClaimedTask;
    }

    public void setLongExpireTimeoutForClaimedTask(Long expireTimeoutForClaimedTask) {
        longExpireTimeoutForClaimedTask = expireTimeoutForClaimedTask;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }


    public String getClaimed() {
        return claimed;
    }

    public void setClaimed(String claimed) {
        this.claimed = claimed;
    }

    public String getRedoDate() {
        return redoDate;
    }

    public void setRedoDate(String redoDate) {
        this.redoDate = redoDate;
    }

    public Boolean getStartedStatusSent() {
        return startedStatusSent;
    }

    public void setStartedStatusSent(Boolean startedStatusSent) {
        this.startedStatusSent = startedStatusSent;
    }

    public Long getLongRedoDateTime() {
        return longRedoDateTime;
    }

    public void setLongRedoDateTime(Long longRedoDateTime) {
        this.longRedoDateTime = longRedoDateTime;
    }

    public Long getLongClaimDateTime() {
        return longClaimDateTime;
    }

    public void setLongClaimDateTime(Long longClaimDateTime) {
        this.longClaimDateTime = longClaimDateTime;
    }

    public Integer getApproxMissionDuration() {
        return approxMissionDuration;
    }

    public void setApproxMissionDuration(Integer approxMissionDuration) {
        this.approxMissionDuration = approxMissionDuration;
    }

    public String getCurrencySign() {
        return currencySign;
    }

    public void setCurrencySign(String currencySign) {
        this.currencySign = currencySign;
    }

    public String getLocationName() {
        return locationName==null?"":locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(String rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getLongStartDateTime() {
        return longStartDateTime;
    }

    public void setLongStartDateTime(Long longStartDateTime) {
        this.longStartDateTime = longStartDateTime;
    }

    public Long getLongPreClaimedTaskExpireAfterStart() {
        return longPreClaimedTaskExpireAfterStart == null ? 0 : longPreClaimedTaskExpireAfterStart;
    }

    public void setLongPreClaimedTaskExpireAfterStart(Long longPreClaimedTaskExpireAfterStart) {
        this.longPreClaimedTaskExpireAfterStart = longPreClaimedTaskExpireAfterStart;
    }

    public Double getLongitudeToValidation() {
        return longitudeToValidation == null ? 0 : longitudeToValidation;
    }

    public void setLongitudeToValidation(Double longitudeToValidation) {
        this.longitudeToValidation = longitudeToValidation;
    }

    public Double getLatitudeToValidation() {
        return latitudeToValidation == null ? 0 : latitudeToValidation;
    }

    public void setLatitudeToValidation(Double latitudeToValidation) {
        this.latitudeToValidation = latitudeToValidation;
    }

    public Long getLongExpireDateTime() {
        return longExpireDateTime == null ? 0 : longExpireDateTime;
    }

    public void setLongExpireDateTime(Long longExpireDateTime) {
        this.longExpireDateTime = longExpireDateTime;
    }

    public String getExpireDateTime() {
        return expireDateTime;
    }

    public void setExpireDateTime(String expireDateTime) {
        this.expireDateTime = expireDateTime;
    }

    public String getFeedBackShort() {
        return feedBackShort == null ? "" : feedBackShort;
    }

    public void setFeedBackShort(String feedBackShort) {
        this.feedBackShort = feedBackShort;
    }


    public String getFeedBackCommentFormatted() {
        return feedBackCommentFormatted == null ? "" : feedBackCommentFormatted;
    }

    public void setFeedBackCommentFormatted(String feedBackCommentFormatted) {
        this.feedBackCommentFormatted = feedBackCommentFormatted;
    }


    /**
     * Return {@link com.google.android.gms.maps.model.LatLng} object with {@link Task}
     * coordinates
     *
     * @return
     */
    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }

    public com.baidu.mapapi.model.LatLng getBaiduLatLng() {
        return new com.baidu.mapapi.model.LatLng(this.latitude, this.longitude);
    }

    @Override
    public String toString() {
        return "Task{ waveId=" + waveId + ", userId='" + userId + '\'' + ", name='" + name + '\''
                + ", description='" + description + '\'' + ", language=" + language
                + ", latitude='" + latitude + '\'' + ", longitude='" + longitude + '\''
                + ", price='" + price + '\'' + ", address=" + address + '}';
    }


}
