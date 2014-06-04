package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.SurveyDbSchema;

public class Survey extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    public enum SurveyTypes {
        none(0), type1(1), type2(2), type3(3), type4(4), type5(5);

        private int id;

        private SurveyTypes(int typeId) {
            this.id = typeId;
        }

        public int getId() {
            return id;
        }
    }

    private Boolean ClaimableBeforeLive;
    private Integer ConcurrentClaimsPerAgent;
    private String Description;
    private String EndDateTime;
    private String ExpectedEndDateTime;
    private String ExpectedStartDateTime;
    private String ExternalId;
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

    private transient Float Longitude;
    private transient Float Latitude;

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
            result.setId(c.getInt(SurveyDbSchema.Query.ID));
            result.setName(c.getString(SurveyDbSchema.Query.NAME));
            result.setDescription(c.getString(SurveyDbSchema.Query.DESCRIPTION));
            result.setLongitude(c.getFloat(SurveyDbSchema.Query.LONGITUDE));
            result.setLatitude(c.getFloat(SurveyDbSchema.Query.LATITUDE));

            result.setClaimableBeforeLive(c.getInt(SurveyDbSchema.Query.CLAIMABLE_BEFORE_LIVE) == 1);
            result.setViewableBeforeLive(c.getInt(SurveyDbSchema.Query.VIEWABLE_BEFORE_LIVE) == 1);
            result.setConcurrentClaimsPerAgent(c.getInt(SurveyDbSchema.Query.CONCURRENT_CLAIMS_PER_AGENT));
            result.setExternalId(c.getString(SurveyDbSchema.Query.EXTERNAL_ID));
            result.setStartDateTime(c.getString(SurveyDbSchema.Query.START_DATE_TIME));
            result.setSuspensionTarget(c.getInt(SurveyDbSchema.Query.SUSPENSION_TARGET));
            result.setTargetMaximum(c.getInt(SurveyDbSchema.Query.TARGET_MAXIMUM));
            result.setTargetMinimum(c.getInt(SurveyDbSchema.Query.TARGET_MINIMUM));

            result.setMaximumClaimsPerAgent(c.getInt(SurveyDbSchema.Query.MAXIMUM_CLAIMS_PER_AGENT));
            result.setEndDateTime(c.getString(SurveyDbSchema.Query.END_DATE_TIME));
            result.setExpectedEndDateTime(c.getString(SurveyDbSchema.Query.EXPECTED_END_DATE_TIME));
            result.setExpectedStartDateTime(c.getString(SurveyDbSchema.Query.EXPECTED_START_DATE_TIME));
            result.setExperienceOffer(c.getDouble(SurveyDbSchema.Query.EXPERIENCE_OFFER));

            result.setExpireTimeoutForClaimedTask(c.getInt(SurveyDbSchema.Query.EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setPreClaimedTaskExpireAfterStart(c.getInt(SurveyDbSchema.Query
                    .PRE_CLAIMED_TASK_EXPIRE_AFTER_START));
        }
        return result;
    }

    public static Survey fromCursorByDistance(Cursor c) {
        Survey result = new Survey();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(SurveyDbSchema.QuerySurveyByDistance._ID));
            result.setId(c.getInt(SurveyDbSchema.QuerySurveyByDistance.ID));
            result.setName(c.getString(SurveyDbSchema.QuerySurveyByDistance.NAME));
            result.setDescription(c.getString(SurveyDbSchema.QuerySurveyByDistance.DESCRIPTION));
            result.setLongitude(c.getFloat(SurveyDbSchema.QuerySurveyByDistance.LONGITUDE));
            result.setLatitude(c.getFloat(SurveyDbSchema.QuerySurveyByDistance.LATITUDE));

            result.setClaimableBeforeLive(c.getInt(SurveyDbSchema.QuerySurveyByDistance.CLAIMABLE_BEFORE_LIVE) == 1);
            result.setViewableBeforeLive(c.getInt(SurveyDbSchema.QuerySurveyByDistance.VIEWABLE_BEFORE_LIVE) == 1);
            result.setConcurrentClaimsPerAgent(c.getInt(SurveyDbSchema.QuerySurveyByDistance
                    .CONCURRENT_CLAIMS_PER_AGENT));
            result.setExternalId(c.getString(SurveyDbSchema.QuerySurveyByDistance.EXTERNAL_ID));
            result.setStartDateTime(c.getString(SurveyDbSchema.QuerySurveyByDistance.START_DATE_TIME));
            result.setSuspensionTarget(c.getInt(SurveyDbSchema.QuerySurveyByDistance.SUSPENSION_TARGET));
            result.setTargetMaximum(c.getInt(SurveyDbSchema.QuerySurveyByDistance.TARGET_MAXIMUM));
            result.setTargetMinimum(c.getInt(SurveyDbSchema.QuerySurveyByDistance.TARGET_MINIMUM));

            result.setMaximumClaimsPerAgent(c.getInt(SurveyDbSchema.QuerySurveyByDistance.MAXIMUM_CLAIMS_PER_AGENT));
            result.setEndDateTime(c.getString(SurveyDbSchema.QuerySurveyByDistance.END_DATE_TIME));
            result.setExpectedEndDateTime(c.getString(SurveyDbSchema.QuerySurveyByDistance.EXPECTED_END_DATE_TIME));
            result.setExpectedStartDateTime(c.getString(SurveyDbSchema.QuerySurveyByDistance.EXPECTED_START_DATE_TIME));

            result.setNearTaskId(c.getInt(SurveyDbSchema.QuerySurveyByDistance.NEAR_TASK_ID));
            result.setNearTaskDistance(c.getFloat(SurveyDbSchema.QuerySurveyByDistance.NEAR_TASK_DISTANCE));
            result.setTaskCount(c.getInt(SurveyDbSchema.QuerySurveyByDistance.TASK_COUNT));
            result.setNearTaskPrice(c.getDouble(SurveyDbSchema.QuerySurveyByDistance.NEAR_TASK_PRICE));

            result.setExperienceOffer(c.getDouble(SurveyDbSchema.QuerySurveyByDistance.EXPERIENCE_OFFER));

            result.setExpireTimeoutForClaimedTask(c.getInt(SurveyDbSchema.Query.EXPIRE_TIMEOUT_FOR_CLAIMED_TASK));
            result.setPreClaimedTaskExpireAfterStart(c.getInt(SurveyDbSchema.Query
                    .PRE_CLAIMED_TASK_EXPIRE_AFTER_START));

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

    public String getExternalId() {
        return ExternalId;
    }

    public void setExternalId(String externalId) {
        ExternalId = externalId;
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
}
