package com.ros.smartrocket.db.entity;

import android.database.Cursor;

import com.ros.smartrocket.db.WaveDbSchema;

public class Wave extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    public enum WaveTypes {
        NONE(0), TYPE_1(1), TYPE_2(2), TYPE_3(3), TYPE_4(4), TYPE_5(5);

        private int id;

        private WaveTypes(int typeId) {
            this.id = typeId;
        }

        public int getId() {
            return id;
        }
    }

    private Boolean ClaimableBeforeLive;
    private Boolean IsCanBePreClaimed;
    private Integer ConcurrentClaimsPerAgent;
    private String Description;
    private String EndDateTime;
    private String ExpectedEndDateTime;
    private String ExpectedStartDateTime;
    private String ExternalWaveId;
    private Integer MaximumClaimsPerAgent;
    private String Name;
    private String StartDateTime;
    private Integer SuspensionTarget;
    private Integer TargetMaximum;
    private Integer TargetMinimum;
    private Boolean ViewableBeforeLive;
    private Double ExperienceOffer;
    private Integer ExpireTimeoutForClaimedTask;
    private Integer PreClaimedTaskExpireAfterStart;
    private Long LongExpireTimeoutForClaimedTask;
    private Integer PhotoQuestionsCount;
    private Integer NoPhotoQuestionsCount;

    private String Icon;

    private transient Float Longitude;
    private transient Float Latitude;
    private transient Long LongPreClaimedTaskExpireAfterStart;
    private transient Long LongStartDateTime;

    @SkipFieldInContentValues
    private Task[] Tasks;

    @SkipFieldInContentValues
    private Float NearTaskDistance;

    @SkipFieldInContentValues
    private int TaskCount;

    @SkipFieldInContentValues
    private Double NearTaskPrice;

    @SkipFieldInContentValues
    private Integer NearTaskId;

    @SkipFieldInContentValues
    private Country Country;

    @SkipFieldInContentValues
    private Project Project;

    @SkipFieldInContentValues
    private transient Boolean IsAllTaskHide = false;

    @SkipFieldInContentValues
    private String NearTaskCurrencySign;


    public Wave() {
    }

    public Wave(String name, String description) {
        this.Name = name;
        this.Description = description;
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

            result.setClaimableBeforeLive(c.getInt(WaveDbSchema.Query.CLAIMABLE_BEFORE_LIVE) == 1);
            result.setIsCanBePreClaimed(c.getInt(WaveDbSchema.Query.CAN_BE_PRE_CLAIMED) == 1);
            result.setViewableBeforeLive(c.getInt(WaveDbSchema.Query.VIEWABLE_BEFORE_LIVE) == 1);
            result.setConcurrentClaimsPerAgent(c.getInt(WaveDbSchema.Query.CONCURRENT_CLAIMS_PER_AGENT));
            result.setExternalWaveId(c.getString(WaveDbSchema.Query.EXTERNAL_WAVE_ID));
            result.setStartDateTime(c.getString(WaveDbSchema.Query.START_DATE_TIME));
            result.setSuspensionTarget(c.getInt(WaveDbSchema.Query.SUSPENSION_TARGET));
            result.setTargetMaximum(c.getInt(WaveDbSchema.Query.TARGET_MAXIMUM));
            result.setTargetMinimum(c.getInt(WaveDbSchema.Query.TARGET_MINIMUM));

            result.setMaximumClaimsPerAgent(c.getInt(WaveDbSchema.Query.MAXIMUM_CLAIMS_PER_AGENT));
            result.setEndDateTime(c.getString(WaveDbSchema.Query.END_DATE_TIME));
            result.setExpectedEndDateTime(c.getString(WaveDbSchema.Query.EXPECTED_END_DATE_TIME));
            result.setExpectedStartDateTime(c.getString(WaveDbSchema.Query.EXPECTED_START_DATE_TIME));
            result.setExperienceOffer(c.getDouble(WaveDbSchema.Query.EXPERIENCE_OFFER));

            result.setLongExpireTimeoutForClaimedTask(c.getLong(WaveDbSchema.Query.LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setExpireTimeoutForClaimedTask(c.getInt(WaveDbSchema.Query.EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setPreClaimedTaskExpireAfterStart(c.getInt(WaveDbSchema.Query.PRE_CLAIMED_TASK_EXPIRE_AFTER_START));

            result.setPhotoQuestionsCount(c.getInt(WaveDbSchema.Query.PHOTO_QUESTIONS_COUNT));
            result.setNoPhotoQuestionsCount(c.getInt(WaveDbSchema.Query.NO_PHOTO_QUESTIONS_COUNT));

            result.setIcon(c.getString(WaveDbSchema.Query.ICON));

            result.setLongStartDateTime(c.getLong(WaveDbSchema.Query.LONG_START_DATE_TIME));
            result.setLongPreClaimedTaskExpireAfterStart(c.getLong(WaveDbSchema.Query.LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START));
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

            result.setClaimableBeforeLive(c.getInt(WaveDbSchema.QueryWaveByDistance.CLAIMABLE_BEFORE_LIVE) == 1);
            result.setIsCanBePreClaimed(c.getInt(WaveDbSchema.QueryWaveByDistance.CAN_BE_PRE_CLAIMED) == 1);
            result.setViewableBeforeLive(c.getInt(WaveDbSchema.QueryWaveByDistance.VIEWABLE_BEFORE_LIVE) == 1);
            result.setConcurrentClaimsPerAgent(c.getInt(WaveDbSchema.QueryWaveByDistance
                    .CONCURRENT_CLAIMS_PER_AGENT));
            result.setExternalWaveId(c.getString(WaveDbSchema.QueryWaveByDistance.EXTERNAL_ID));
            result.setStartDateTime(c.getString(WaveDbSchema.QueryWaveByDistance.START_DATE_TIME));
            result.setSuspensionTarget(c.getInt(WaveDbSchema.QueryWaveByDistance.SUSPENSION_TARGET));
            result.setTargetMaximum(c.getInt(WaveDbSchema.QueryWaveByDistance.TARGET_MAXIMUM));
            result.setTargetMinimum(c.getInt(WaveDbSchema.QueryWaveByDistance.TARGET_MINIMUM));

            result.setMaximumClaimsPerAgent(c.getInt(WaveDbSchema.QueryWaveByDistance.MAXIMUM_CLAIMS_PER_AGENT));
            result.setEndDateTime(c.getString(WaveDbSchema.QueryWaveByDistance.END_DATE_TIME));
            result.setExpectedEndDateTime(c.getString(WaveDbSchema.QueryWaveByDistance.EXPECTED_END_DATE_TIME));
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
            result.setPhotoQuestionsCount(c.getInt(WaveDbSchema.QueryWaveByDistance.PHOTO_QUESTIONS_COUNT));
            result.setNoPhotoQuestionsCount(c.getInt(WaveDbSchema.QueryWaveByDistance.NO_PHOTO_QUESTIONS_COUNT));
            result.setIsAllTaskHide(c.getInt(WaveDbSchema.QueryWaveByDistance.IS_ALL_TASK_HIDE) == 1);
            result.setNearTaskCurrencySign(c.getString(WaveDbSchema.QueryWaveByDistance.NEAR_TASK_CURRENCY_SIGN));
            result.setIcon(c.getString(WaveDbSchema.QueryWaveByDistance.ICON));

            result.setLongStartDateTime(c.getLong(WaveDbSchema.QueryWaveByDistance.LONG_START_DATE_TIME));
            result.setLongPreClaimedTaskExpireAfterStart(c.getLong(WaveDbSchema.QueryWaveByDistance.LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START));

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

    public Boolean getClaimableBeforeLive() {
        return ClaimableBeforeLive;
    }

    public void setClaimableBeforeLive(Boolean claimableBeforeLive) {
        ClaimableBeforeLive = claimableBeforeLive;
    }

    public Boolean getViewableBeforeLive() {
        return ViewableBeforeLive;
    }

    public void setViewableBeforeLive(Boolean viewableBeforeLive) {
        ViewableBeforeLive = viewableBeforeLive;
    }

    public Integer getConcurrentClaimsPerAgent() {
        return ConcurrentClaimsPerAgent;
    }

    public void setConcurrentClaimsPerAgent(Integer concurrentClaimsPerAgent) {
        ConcurrentClaimsPerAgent = concurrentClaimsPerAgent;
    }

    public String getExternalWaveId() {
        return ExternalWaveId;
    }

    public void setExternalWaveId(String externalWaveId) {
        ExternalWaveId = externalWaveId;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        StartDateTime = startDateTime;
    }

    public Integer getSuspensionTarget() {
        return SuspensionTarget;
    }

    public void setSuspensionTarget(Integer suspensionTarget) {
        SuspensionTarget = suspensionTarget;
    }

    public Integer getTargetMaximum() {
        return TargetMaximum;
    }

    public void setTargetMaximum(Integer targetMaximum) {
        TargetMaximum = targetMaximum;
    }

    public Integer getTargetMinimum() {
        return TargetMinimum;
    }

    public void setTargetMinimum(Integer targetMinimum) {
        TargetMinimum = targetMinimum;
    }

    public Integer getMaximumClaimsPerAgent() {
        return MaximumClaimsPerAgent;
    }

    public void setMaximumClaimsPerAgent(Integer maximumClaimsPerAgent) {
        MaximumClaimsPerAgent = maximumClaimsPerAgent;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    public String getExpectedEndDateTime() {
        return ExpectedEndDateTime;
    }

    public void setExpectedEndDateTime(String expectedEndDateTime) {
        ExpectedEndDateTime = expectedEndDateTime;
    }

    public String getExpectedStartDateTime() {
        return ExpectedStartDateTime;
    }

    public void setExpectedStartDateTime(String expectedStartDateTime) {
        ExpectedStartDateTime = expectedStartDateTime;
    }

    public Task[] getTasks() {
        return Tasks;
    }

    public void setTasks(Task[] tasks) {
        this.Tasks = tasks;
    }

    public Float getNearTaskDistance() {
        return NearTaskDistance;
    }

    public void setNearTaskDistance(Float nearTaskDistance) {
        NearTaskDistance = nearTaskDistance;
    }

    public int getTaskCount() {
        return TaskCount;
    }

    public void setTaskCount(int taskCount) {
        TaskCount = taskCount;
    }

    public Double getNearTaskPrice() {
        return NearTaskPrice;
    }

    public void setNearTaskPrice(Double nearTaskPrice) {
        NearTaskPrice = nearTaskPrice;
    }

    public Integer getNearTaskId() {
        return NearTaskId;
    }

    public void setNearTaskId(Integer nearTaskId) {
        NearTaskId = nearTaskId;
    }

    public Double getExperienceOffer() {
        return ExperienceOffer;
    }

    public void setExperienceOffer(Double experienceOffer) {
        ExperienceOffer = experienceOffer;
    }

    public Integer getPreClaimedTaskExpireAfterStart() {
        return PreClaimedTaskExpireAfterStart;
    }

    public void setPreClaimedTaskExpireAfterStart(Integer preClaimedTaskExpireAfterStart) {
        PreClaimedTaskExpireAfterStart = preClaimedTaskExpireAfterStart;
    }

    public Integer getExpireTimeoutForClaimedTask() {
        return ExpireTimeoutForClaimedTask;
    }

    public void setExpireTimeoutForClaimedTask(Integer expireTimeoutForClaimedTask) {
        ExpireTimeoutForClaimedTask = expireTimeoutForClaimedTask;
    }

    public Country getCountry() {
        return Country;
    }

    public void setCountry(Country country) {
        Country = country;
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

    public Long getLongExpireTimeoutForClaimedTask() {
        return LongExpireTimeoutForClaimedTask == null ? 0 : LongExpireTimeoutForClaimedTask;
    }

    public void setLongExpireTimeoutForClaimedTask(Long longExpireTimeoutForClaimedTask) {
        LongExpireTimeoutForClaimedTask = longExpireTimeoutForClaimedTask;
    }

    public Boolean getIsAllTaskHide() {
        return IsAllTaskHide;
    }

    public void setIsAllTaskHide(Boolean isAllMissionHide) {
        IsAllTaskHide = isAllMissionHide;
    }


    public String getNearTaskCurrencySign() {
        return NearTaskCurrencySign;
    }

    public void setNearTaskCurrencySign(String nearTaskCurrencySign) {
        NearTaskCurrencySign = nearTaskCurrencySign;
    }


    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public Project getProject() {
        return Project;
    }

    public void setProject(Project project) {
        Project = project;
    }

    public Long getLongStartDateTime() {
        return LongStartDateTime;
    }

    public void setLongStartDateTime(Long longStartDateTime) {
        LongStartDateTime = longStartDateTime;
    }

    public Long getLongPreClaimedTaskExpireAfterStart() {
        return LongPreClaimedTaskExpireAfterStart == null ? 0 : LongPreClaimedTaskExpireAfterStart;
    }

    public void setLongPreClaimedTaskExpireAfterStart(Long longPreClaimedTaskExpireAfterStart) {
        LongPreClaimedTaskExpireAfterStart = longPreClaimedTaskExpireAfterStart;
    }

    public Boolean getIsCanBePreClaimed() {
        return IsCanBePreClaimed;
    }

    public void setIsCanBePreClaimed(Boolean isCanBePreClaimed) {
        IsCanBePreClaimed = isCanBePreClaimed;
    }

}
