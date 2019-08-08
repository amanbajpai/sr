package com.ros.smartrocket.db;

import android.net.Uri;

public interface CustomFieldImageUrlDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns._ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.CUSTOM_FIELD_IMAGE_URL
            .getName()).build();

    String SORT_ORDER_ASC = Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns._ID.getName() + " ASC";

    enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        QUESTION_ID("questionId", DBType.NUMERIC),
        TASK_ID("taskId", DBType.NUMERIC),
        MISSION_ID("missionId", DBType.NUMERIC),
        PRODUCT_ID("productId", DBType.NUMERIC),
        IMAGE_URL("imageUrl", DBType.TEXT),
        DELETED("deleted", DBType.TEXT);

        private String columnName;
        private DBType type;

        Columns(String columnName, DBType type) {
            this.columnName = columnName;
            this.type = type;
        }

        /**
         * @return String
         */
        public String getName() {
            return columnName;
        }

        /**
         * @return DBType
         */
        public DBType getType() {
            return type;
        }

        /**
         * @return String
         */
        public String toString() {
            return columnName;
        }
    }

    public interface Query {
        int TOKEN_QUERY = 31;
        int TOKEN_UPDATE = 33;

        String[] PROJECTION = {
                Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns._ID.getName(),
                Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns.ID.getName(),
                Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns.QUESTION_ID.getName(),
                Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns.TASK_ID.getName(),
                Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns.MISSION_ID.getName(),
                Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns.PRODUCT_ID.getName(),
                Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns.IMAGE_URL.getName(),
                Table.CUSTOM_FIELD_IMAGE_URL.getName() + "." + Columns.DELETED.getName()

        };

        int _ID = 0;
        int ID = 1;
        int QUESTION_ID = 2;
        int TASK_ID = 3;
        int MISSION_ID = 4;
        int PRODUCT_ID = 5;
        int IMAGE_URL = 6;
        int DELETED = 7;
    }
}
