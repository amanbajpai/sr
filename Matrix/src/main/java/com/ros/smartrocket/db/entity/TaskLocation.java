package com.ros.smartrocket.db.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class TaskLocation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    @SerializedName("State")
    private String state;
    @SerializedName("StateId")
    private Integer stateId;
    @SerializedName("City")
    private String city;
    @SerializedName("CityId")
    private Integer cityId;
    @SerializedName("RetailerName")
    private String retailerName;

    private String customFields;
    @SkipFieldInContentValues
    @SerializedName("CustomFields")
    private Map<String, String> customFieldsMap;

    public TaskLocation() {
    }

    public static TaskLocation getTaskLocation(String jsonString) {
        Gson gson = new Gson();
        TaskLocation taskLocation = gson.fromJson(jsonString, TaskLocation.class);
        taskLocation.setCustomFields(gson.toJson(taskLocation.getCustomFieldsMap()));
        return taskLocation;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getCustomFields() {
        return customFields;
    }

    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }

    public Map<String, String> getCustomFieldsMap() {
        return customFieldsMap;
    }

    public void setCustomFieldsMap(Map<String, String> customFieldsMap) {
        this.customFieldsMap = customFieldsMap;
    }
}