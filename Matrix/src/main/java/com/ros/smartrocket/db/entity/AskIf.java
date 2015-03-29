package com.ros.smartrocket.db.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class AskIf extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -4706526633427191907L;

    public enum ConditionSourceType {
        LOCATION_RETAILER(1),
        LOCATION_STATE(2),
        LOCATION_CITY(3),
        CUSTOM_FIELD(4),
        PREV_QUESTION(5),
        ROUTING(6);

        private int typeId;

        private ConditionSourceType(int statusId) {
            this.typeId = statusId;
        }

        public int getTypeId() {
            return typeId;
        }

        public static ConditionSourceType getSourceTypeById(int typeId) {
            ConditionSourceType result = LOCATION_RETAILER;
            for (ConditionSourceType type : ConditionSourceType.values()) {
                if (type.getTypeId() == typeId) {
                    result = type;
                    break;
                }
            }
            return result;
        }
    }

    @SerializedName("OrderId")
    private Integer orderId;
    @SerializedName("SourceType")
    private Integer sourceType;
    @SerializedName("SourceKey")
    private String sourceKey;
    @SerializedName("Value")
    private String value;
    @SerializedName("Operator")
    private Integer operator;
    @SerializedName("NextConditionOperator")
    private Integer nextConditionOperator;

    public AskIf() {
    }

    public static AskIf[] getAskIfArray(String jsonArrayString) {
        AskIfs askIfs = new Gson().fromJson(jsonArrayString, AskIfs.class);
        return sortAskIfByOrderId(askIfs.getAskIfs());
    }

    public static AskIf[] sortAskIfByOrderId(AskIf[] askIfArray) {
        Arrays.sort(askIfArray, new Comparator<AskIf>() {
            public int compare(AskIf o1, AskIf o2) {
                return String.valueOf(o1.getOrderId()).compareTo(String.valueOf(o2.getOrderId()));
            }
        });

        return askIfArray;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getNextConditionOperator() {
        return nextConditionOperator;
    }

    public void setNextConditionOperator(Integer nextConditionOperator) {
        this.nextConditionOperator = nextConditionOperator;
    }

}