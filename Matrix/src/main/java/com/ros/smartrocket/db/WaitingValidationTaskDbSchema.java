package com.ros.smartrocket.db;

import android.net.Uri;

public interface WaitingValidationTaskDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table
            .WAITING_VALIDATION_TASK.getName()).build();

    String SORT_ORDER_ASC_LIMIT_1 = Table.WAITING_VALIDATION_TASK.getName() + "." + Columns._ID.getName() + " ASC LIMIT 1";
    String SORT_ORDER_DESC = Table.WAITING_VALIDATION_TASK.getName() + "." + Columns._ID.getName() + " DESC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        TASK_ID("taskId", DBType.NUMERIC),
        QUESTION_ID("questionId", DBType.NUMERIC),
        MISSION_ID("missionId", DBType.NUMERIC),
        ADDED_TO_UPLOAD_DATE_TIME("addedToUploadDateTime", DBType.NUMERIC),
        CITY_NAME("cityName", DBType.TEXT),
        LATITUDE_TO_VALIDATION("latitudeToValidation", DBType.TEXT),
        LONGITUDE_TO_VALIDATION("longitudeToValidation", DBType.TEXT),
        ALL_FILE_SENT("allFileSent", DBType.NUMERIC),

        DELETED("deleted", DBType.INT);

        private String columnName;
        private DBType type;

        Columns(String columnName, DBType type) {
            this.columnName = columnName;
            this.type = type;
        }

        /**
         * @return
         */
        public String getName() {
            return columnName;
        }

        /**
         * @return
         */
        public DBType getType() {
            return type;
        }

        /**
         * @return
         */
        public String toString() {
            return this.getName();
        }
    }

    interface Query {
        int TOKEN_QUERY = 51;

        String[] PROJECTION = {Table.WAITING_VALIDATION_TASK.getName() + "." + Columns._ID.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.ID.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.TASK_ID.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.QUESTION_ID.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.MISSION_ID.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.ADDED_TO_UPLOAD_DATE_TIME.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.CITY_NAME.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.LATITUDE_TO_VALIDATION.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.LONGITUDE_TO_VALIDATION.getName(),
                Table.WAITING_VALIDATION_TASK.getName() + "." + Columns.ALL_FILE_SENT.getName()
        };

        int _ID = 0;
        int ID = 1;
        int TASK_ID = 2;
        int QUESTION_ID = 3;
        int MISSION_ID = 4;
        int ADDED_TO_UPLOAD_DATE_TIME = 5;
        int CITY_NAME = 6;
        int LATITUDE_TO_VALIDATION = 7;
        int LONGITUDE_TO_VALIDATION = 8;
        int ALL_FILE_SENT = 9;
    }
}
