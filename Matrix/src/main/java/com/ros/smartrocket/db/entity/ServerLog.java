package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class ServerLog extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    public enum LogType {
        FILE_UPLOAD("FileUpload"),
        PACKAGE_UPLOAD("PackageUpload"),
        ANSWERS_UPLOAD("AnswersUpload"),
        VALIDATE_TASK("ValidateTask");

        private String logType;

        LogType(String logType) {
            this.logType = logType;
        }

        public String getType() {
            return logType;
        }
    }

    @SerializedName("UserName")
    private String userName;
    @SerializedName("LogMessage")
    private String logMessage;
    @SerializedName("LogType")
    private String logType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }
}
