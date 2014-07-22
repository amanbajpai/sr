package com.ros.smartrocket.db;

import android.net.Uri;

public interface NotUploadedFileDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table
            .NOT_UPLOADED_FILE.getName()).build();

    String SORT_ORDER_ASC_LIMIT_1 = Table.NOT_UPLOADED_FILE.getName() + "." + Columns._ID.getName() + " ASC LIMIT 1";
    String SORT_ORDER_DESC = Table.NOT_UPLOADED_FILE.getName() + "." + Columns._ID.getName() + " DESC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        TASK_ID("TaskId", DBType.NUMERIC),
        QUESTION_ID("QuestionId", DBType.NUMERIC),
        FILE_URI("FileUri", DBType.TEXT),
        ADDED_TO_UPLOAD_DATE_TIME("AddedToUploadDateTime", DBType.NUMERIC),
        END_DATE_TIME("EndDateTime", DBType.NUMERIC),
        USE_3G("use3G", DBType.INT),
        FILE_SIZE_B("fileSizeB", DBType.NUMERIC),
        SHOW_NOTIFICATION_STEP_ID("showNotificationStepId", DBType.INT),

        PORTION("Portion", DBType.INT),
        FILE_CODE("FileCode", DBType.TEXT),
        FILE_NAME("FileName", DBType.TEXT),

        TASK_NAME("TaskName", DBType.TEXT),

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

    public interface Query {
        int TOKEN_QUERY = 41;

        String[] PROJECTION = {Table.NOT_UPLOADED_FILE.getName() + "." + Columns._ID.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.ID.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.TASK_ID.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.QUESTION_ID.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.FILE_URI.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.ADDED_TO_UPLOAD_DATE_TIME.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.END_DATE_TIME.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.USE_3G.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.FILE_SIZE_B.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.SHOW_NOTIFICATION_STEP_ID.getName(),

                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.PORTION.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.FILE_CODE.getName(),
                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.FILE_NAME.getName(),

                Table.NOT_UPLOADED_FILE.getName() + "." + Columns.TASK_NAME.getName()
        };

        int _ID = 0;
        int ID = 1;
        int TASK_ID = 2;
        int QUESTION_ID = 3;
        int FILE_URI = 4;
        int ADDED_TO_UPLOAD_DATE_TIME = 5;
        int END_DATE_TIME = 6;
        int USE_3G = 7;
        int FILE_SIZE_B = 8;
        int SHOW_NOTIFICATION_STEP_ID = 9;

        int PORTION = 10;
        int FILE_CODE = 11;
        int FILE_NAME = 12;

        int TASK_NAME = 13;
    }
}
