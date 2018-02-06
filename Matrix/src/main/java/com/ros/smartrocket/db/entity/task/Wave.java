package com.ros.smartrocket.db.entity.task;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.db.entity.Country;

public class Wave extends BaseEntity {
    @SkipFieldInContentValues
    private static final long serialVersionUID = 5410835468659163958L;
    @SerializedName("IsCanBePreClaimed")
    private Boolean isCanBePreClaimed;
    @SerializedName("DescriptionFormatted")
    private String description;
    @SerializedName("EndDateTime")
    private String endDateTime;
    private String expectedStartDateTime;
    @SerializedName("Name")
    private String name;
    @SerializedName("ExperienceOffer")
    private Double experienceOffer;
    @SerializedName("ApproxMissionDuration")
    private Integer approxMissionDuration;
    @SerializedName("Icon")
    private String icon;

    @SerializedName("Longitude")
    private Float longitude;
    @SerializedName("Latitude")
    private Float latitude;

    @SerializedName("StartDateTime")
    private String startDateTime;
    @SerializedName("PreClaimedTaskExpireAfterStart")
    private Integer preClaimedTaskExpireAfterStart;
    @SerializedName("ExpireTimeoutForClaimedTask")
    private Integer expireTimeoutForClaimedTask;

    @SerializedName("LongStartDateTime")
    private Long longStartDateTime;
    @SerializedName("LongExpireTimeoutForClaimedTask")
    private Long longExpireTimeoutForClaimedTask;
    @SerializedName("LongPreClaimedTaskExpireAfterStart")
    private Long longPreClaimedTaskExpireAfterStart;
    @SerializedName("DownloadMediaWhenClaimingTask")
    private Boolean downloadMediaWhenClaimingTask;

    // Id Card
    @SerializedName("IdCardStatus")
    private Integer idCardStatus;
    @SerializedName("IdCardLogo")
    private String idCardLogo;
    @SerializedName("IdCardText")
    private String idCardText;

    @SerializedName("MissionSize")
    private Integer missionSize;

    @SkipFieldInContentValues
    @SerializedName("Tasks")
    private Task[] tasks;

    @SkipFieldInContentValues
    private Float nearTaskDistance;

    @SkipFieldInContentValues
    private int taskCount;

    @SkipFieldInContentValues
    private Double nearTaskPrice;

    @SkipFieldInContentValues
    private Integer nearTaskId;

    @SkipFieldInContentValues
    @SerializedName("Country")
    private Country country;

    @SkipFieldInContentValues
    @SerializedName("Project")
    private Project project;

    @SkipFieldInContentValues
    private transient Boolean isAllTaskHide = false;

    @SkipFieldInContentValues
    private String nearTaskCurrencySign;


    private transient Boolean containsDifferentRate = false;
    private transient Double rate;


    public Wave() {
    }

    public Wave(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Wave fromCursor(Cursor c) {
        Wave result = new Wave();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(WaveDbSchema.Query._ID));
            result.setId(c.getInt(WaveDbSchema.Query.ID));
            result.setName(c.getString(WaveDbSchema.Query.NAME));
            result.setDescription(c.getString(WaveDbSchema.Query.DESCRIPTION));
            result.setLongitude(c.getFloat(WaveDbSchema.Query.LONGITUDE));
            result.setLatitude(c.getFloat(WaveDbSchema.Query.LATITUDE));
            result.setIsCanBePreClaimed(c.getInt(WaveDbSchema.Query.CAN_BE_PRE_CLAIMED) == 1);
            result.setStartDateTime(c.getString(WaveDbSchema.Query.START_DATE_TIME));
            result.setEndDateTime(c.getString(WaveDbSchema.Query.END_DATE_TIME));
            result.setExpectedStartDateTime(c.getString(WaveDbSchema.Query.EXPECTED_START_DATE_TIME));
            result.setExperienceOffer(c.getDouble(WaveDbSchema.Query.EXPERIENCE_OFFER));

            result.setLongExpireTimeoutForClaimedTask(c.getLong(WaveDbSchema.Query
                    .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setExpireTimeoutForClaimedTask(c.getInt(WaveDbSchema.Query.EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setPreClaimedTaskExpireAfterStart(c.getInt(WaveDbSchema.Query.PRE_CLAIMED_TASK_EXPIRE_AFTER_START));

            result.setIcon(c.getString(WaveDbSchema.Query.ICON));

            result.setLongStartDateTime(c.getLong(WaveDbSchema.Query.LONG_START_DATE_TIME));
            result.setLongPreClaimedTaskExpireAfterStart(c.getLong(WaveDbSchema.Query
                    .LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START));

            result.setDownloadMediaWhenClaimingTask(c.getInt(WaveDbSchema.Query.DOWNLOAD_MEDIA_WHEN_CLAIMING_TASK) == 1);
            result.setContainsDifferentRate(c.getInt(WaveDbSchema.Query.CONTAINS_DIFFERENT_RATE) == 1);
            result.setRate(c.getDouble(WaveDbSchema.Query.RATE));

            result.setIdCardStatus(c.getInt(WaveDbSchema.Query.ID_CARD_STATUS));
            result.setIdCardLogo(c.getString(WaveDbSchema.Query.ID_CARD_LOGO));
            result.setIdCardText(c.getString(WaveDbSchema.Query.ID_CARD_TEXT));
            result.setApproxMissionDuration(c.getInt(WaveDbSchema.Query.APPROX_MISSION_DURATION));
            result.setMissionSize(c.getInt(WaveDbSchema.Query.MISSION_SIZE));
        }
        return result;
    }

    public static Wave fromCursorByDistance(Cursor c) {
        Wave result = new Wave();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(WaveDbSchema.QueryWaveByDistance._ID));
            result.setId(c.getInt(WaveDbSchema.QueryWaveByDistance.ID));
            result.setName(c.getString(WaveDbSchema.QueryWaveByDistance.NAME));
            result.setDescription(c.getString(WaveDbSchema.QueryWaveByDistance.DESCRIPTION));
            result.setLongitude(c.getFloat(WaveDbSchema.QueryWaveByDistance.LONGITUDE));
            result.setLatitude(c.getFloat(WaveDbSchema.QueryWaveByDistance.LATITUDE));
            result.setIsCanBePreClaimed(c.getInt(WaveDbSchema.QueryWaveByDistance.CAN_BE_PRE_CLAIMED) == 1);
            result.setStartDateTime(c.getString(WaveDbSchema.QueryWaveByDistance.START_DATE_TIME));
            result.setEndDateTime(c.getString(WaveDbSchema.QueryWaveByDistance.END_DATE_TIME));
            result.setExpectedStartDateTime(c.getString(WaveDbSchema.QueryWaveByDistance.EXPECTED_START_DATE_TIME));
            result.setNearTaskId(c.getInt(WaveDbSchema.QueryWaveByDistance.NEAR_TASK_ID));
            result.setNearTaskDistance(c.getFloat(WaveDbSchema.QueryWaveByDistance.NEAR_TASK_DISTANCE));
            result.setTaskCount(c.getInt(WaveDbSchema.QueryWaveByDistance.TASK_COUNT));
            result.setNearTaskPrice(c.getDouble(WaveDbSchema.QueryWaveByDistance.NEAR_TASK_PRICE));
            result.setExperienceOffer(c.getDouble(WaveDbSchema.QueryWaveByDistance.EXPERIENCE_OFFER));
            result.setLongExpireTimeoutForClaimedTask(c.getLong(WaveDbSchema.QueryWaveByDistance
                    .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setExpireTimeoutForClaimedTask(c.getInt(WaveDbSchema.QueryWaveByDistance
                    .EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setPreClaimedTaskExpireAfterStart(c.getInt(WaveDbSchema.QueryWaveByDistance
                    .PRE_CLAIMED_TASK_EXPIRE_AFTER_START));
            result.setIsAllTaskHide(c.getInt(WaveDbSchema.QueryWaveByDistance.IS_ALL_TASK_HIDE) == 1);
            result.setNearTaskCurrencySign(c.getString(WaveDbSchema.QueryWaveByDistance.NEAR_TASK_CURRENCY_SIGN));
            result.setIcon(c.getString(WaveDbSchema.QueryWaveByDistance.ICON));

            result.setLongStartDateTime(c.getLong(WaveDbSchema.QueryWaveByDistance.LONG_START_DATE_TIME));
            result.setLongPreClaimedTaskExpireAfterStart(c.getLong(WaveDbSchema.QueryWaveByDistance
                    .LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START));

            result.setDownloadMediaWhenClaimingTask(c.getInt(WaveDbSchema.QueryWaveByDistance.DOWNLOAD_MEDIA_WHEN_CLAIMING_TASK) == 1);
            result.setContainsDifferentRate(c.getInt(WaveDbSchema.QueryWaveByDistance.CONTAINS_DIFFERENT_RATE) == 1);
            result.setRate(c.getDouble(WaveDbSchema.QueryWaveByDistance.RATE));
            result.setApproxMissionDuration(c.getInt(WaveDbSchema.QueryWaveByDistance.APPROX_MISSION_DURATION));
            result.setMissionSize(c.getInt(WaveDbSchema.QueryWaveByDistance.MISSION_SIZE));
        }
        return result;
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

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
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

    public String getExpectedStartDateTime() {
        return expectedStartDateTime;
    }

    public void setExpectedStartDateTime(String expectedStartDateTime) {
        this.expectedStartDateTime = expectedStartDateTime;
    }

    public Task[] getTasks() {
        return tasks;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }

    public Float getNearTaskDistance() {
        return nearTaskDistance;
    }

    public void setNearTaskDistance(Float nearTaskDistance) {
        this.nearTaskDistance = nearTaskDistance;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public Double getNearTaskPrice() {
        return nearTaskPrice;
    }

    public void setNearTaskPrice(Double nearTaskPrice) {
        this.nearTaskPrice = nearTaskPrice;
    }

    public Integer getNearTaskId() {
        return nearTaskId;
    }

    public void setNearTaskId(Integer nearTaskId) {
        this.nearTaskId = nearTaskId;
    }

    public Double getExperienceOffer() {
        return experienceOffer == null ? 0 : experienceOffer;
    }

    public void setExperienceOffer(Double experienceOffer) {
        this.experienceOffer = experienceOffer;
    }

    public Integer getPreClaimedTaskExpireAfterStart() {
        return preClaimedTaskExpireAfterStart == null ? 0 : preClaimedTaskExpireAfterStart;
    }

    public void setPreClaimedTaskExpireAfterStart(Integer preClaimedTaskExpireAfterStart) {
        this.preClaimedTaskExpireAfterStart = preClaimedTaskExpireAfterStart;
    }

    public Integer getExpireTimeoutForClaimedTask() {
        return expireTimeoutForClaimedTask == null ? 0 : expireTimeoutForClaimedTask;
    }

    public void setExpireTimeoutForClaimedTask(Integer expireTimeoutForClaimedTask) {
        this.expireTimeoutForClaimedTask = expireTimeoutForClaimedTask;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Integer getApproxMissionDuration() {
        return approxMissionDuration;
    }

    public void setApproxMissionDuration(Integer approxMissionDuration) {
        this.approxMissionDuration = approxMissionDuration;
    }

    public Long getLongExpireTimeoutForClaimedTask() {
        return longExpireTimeoutForClaimedTask == null ? 0 : longExpireTimeoutForClaimedTask;
    }

    public void setLongExpireTimeoutForClaimedTask(Long longExpireTimeoutForClaimedTask) {
        this.longExpireTimeoutForClaimedTask = longExpireTimeoutForClaimedTask;
    }

    public Boolean getIsAllTaskHide() {
        return isAllTaskHide;
    }

    public void setIsAllTaskHide(Boolean isAllMissionHide) {
        isAllTaskHide = isAllMissionHide;
    }


    public String getNearTaskCurrencySign() {
        return nearTaskCurrencySign;
    }

    public void setNearTaskCurrencySign(String nearTaskCurrencySign) {
        this.nearTaskCurrencySign = nearTaskCurrencySign;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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

    public Boolean getIsCanBePreClaimed() {
        return isCanBePreClaimed;
    }

    public void setIsCanBePreClaimed(Boolean isCanBePreClaimed) {
        this.isCanBePreClaimed = isCanBePreClaimed;
    }

    public Boolean getDownloadMediaWhenClaimingTask() {
        return downloadMediaWhenClaimingTask;
    }

    public void setDownloadMediaWhenClaimingTask(Boolean downloadMediaWhenClaimingTask) {
        this.downloadMediaWhenClaimingTask = downloadMediaWhenClaimingTask;
    }

    public boolean isContainsDifferentRate() {
        return containsDifferentRate;
    }

    public void setContainsDifferentRate(boolean containsDifferentRate) {
        this.containsDifferentRate = containsDifferentRate;
    }

    public Double getRate() {
        return rate == null ? 0 : rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getIdCardStatus() {
        return idCardStatus == null ? 0 : idCardStatus;
    }

    public void setIdCardStatus(int idCardStatus) {
        this.idCardStatus = idCardStatus;
    }

    public String getIdCardLogo() {
        return idCardLogo;
    }

    public void setIdCardLogo(String idCardLogo) {
        this.idCardLogo = idCardLogo;
    }

    public String getIdCardText() {
        return idCardText;
    }

    public void setIdCardText(String idCardText) {
        this.idCardText = idCardText;
    }

    public Integer getMissionSize() {
        return missionSize;
    }

    public void setMissionSize(Integer missionSize) {
        this.missionSize = missionSize;
    }
}
