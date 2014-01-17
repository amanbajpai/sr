package com.ros.smartrocket.db;

import android.net.Uri;

public interface NotUploadedFileDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table
            .NOT_UPLOADED_FILE.getName()).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.NOT_UPLOADED_FILE.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_DESC = Table.NOT_UPLOADED_FILE.getName() + "." + Columns._ID.getName() + " DESC";
    //String SORT_ORDER = Table.NOT_UPLOADED_FILE.getName() + "." + Columns._ID.getName() + " ASC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        TASK_ID("TaskId", DBType.NUMERIC),
        QUESTION_ID("QuestionId", DBType.NUMERIC),
        FILE_URI("FileUri", DBType.TEXT),
        END_DATE_TIME("EndDateTime", DBType.TEXT),
        USE_3G("use3G", DBType.INT),
        FILE_SIZE_B("fileSizeB", DBType.NUMERIC),

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
        int TOKEN_INSERT = 42;
        int TOKEN_UPDATE = 43;
        //int TOKEN_DELETE = 44;

        String[] PROJECTION = {Table.TASK.getName() + "." + Columns._ID.getName(),
                Table.TASK.getName() + "." + Columns.ID.getName(),
                Table.TASK.getName() + "." + Columns.TASK_ID.getName(),
                Table.TASK.getName() + "." + Columns.QUESTION_ID.getName(),
                Table.TASK.getName() + "." + Columns.FILE_URI.getName(),
                Table.TASK.getName() + "." + Columns.END_DATE_TIME.getName(),
                Table.TASK.getName() + "." + Columns.USE_3G.getName(),
                Table.TASK.getName() + "." + Columns.FILE_SIZE_B.getName()
        };

        int _ID = 0;
        int ID = 1;
        int TASK_ID = 2;
        int QUESTION_ID = 3;
        int FILE_URI = 4;
        int END_DATE_TIME = 5;
        int USE_3G = 6;
        int FILE_SIZE_B = 7;
    }
}
