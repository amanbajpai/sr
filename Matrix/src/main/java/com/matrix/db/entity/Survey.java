package com.matrix.db.entity;

import android.database.Cursor;
import com.matrix.db.SurveyDbSchema;

public class Survey extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

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

    transient private Float Longitude;
    transient private Float Latitude;

    @SkipFieldInContentValues
    private Task[] Tasks;

    @SkipFieldInContentValues
    transient private Float Distance;

    @SkipFieldInContentValues
    transient private int TaskCount;

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
            result.setConcurrentClaimsPerAgent(c.getInt(SurveyDbSchema.QuerySurveyByDistance.CONCURRENT_CLAIMS_PER_AGENT));
            result.setExternalId(c.getString(SurveyDbSchema.QuerySurveyByDistance.EXTERNAL_ID));
            result.setStartDateTime(c.getString(SurveyDbSchema.QuerySurveyByDistance.START_DATE_TIME));
            result.setSuspensionTarget(c.getInt(SurveyDbSchema.QuerySurveyByDistance.SUSPENSION_TARGET));
            result.setTargetMaximum(c.getInt(SurveyDbSchema.QuerySurveyByDistance.TARGET_MAXIMUM));
            result.setTargetMinimum(c.getInt(SurveyDbSchema.QuerySurveyByDistance.TARGET_MINIMUM));

            result.setMaximumClaimsPerAgent(c.getInt(SurveyDbSchema.QuerySurveyByDistance.MAXIMUM_CLAIMS_PER_AGENT));
            result.setEndDateTime(c.getString(SurveyDbSchema.QuerySurveyByDistance.END_DATE_TIME));
            result.setExpectedEndDateTime(c.getString(SurveyDbSchema.QuerySurveyByDistance.EXPECTED_END_DATE_TIME));
            result.setExpectedStartDateTime(c.getString(SurveyDbSchema.QuerySurveyByDistance.EXPECTED_START_DATE_TIME));

            result.setDistance(c.getFloat(SurveyDbSchema.QuerySurveyByDistance.DISTANCE_TO_NEAR));
            result.setTaskCount(c.getInt(SurveyDbSchema.QuerySurveyByDistance.TASK_COUNT));

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

    public Float getDistance() {
        return Distance;
    }

    public void setDistance(Float distance) {
        Distance = distance;
    }

    public int getTaskCount() {
        return TaskCount;
    }

    public void setTaskCount(int taskCount) {
        TaskCount = taskCount;
    }
}
