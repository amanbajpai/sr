package com.ros.smartrocket.db;

import android.net.Uri;

public interface AnswerDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns._ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.ANSWER
            .getName()).build();

    // String SORT_ORDER_DESC_LIMIT_1 = Table.ANSWER.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_ASC = Table.ANSWER.getName() + "." + Columns._ID.getName() + " ASC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        QUESTION_ID("QuestionId", DBType.NUMERIC),
        TASK_ID("TaskId", DBType.NUMERIC),
        ANSWER("Answer", DBType.TEXT),
        VALUE("Value", DBType.TEXT),
        ROUTING("Routing", DBType.NUMERIC),
        CHECKED("Checked", DBType.NUMERIC),

        FILE_URI("FileUri", DBType.TEXT),
        FILE_SIZE_B("FileSizeB", DBType.NUMERIC),
        FILE_NAME("FileName", DBType.TEXT),

        DELETED("deleted", DBType.INT);

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
        //int TOKEN_INSERT = 32;
        int TOKEN_UPDATE = 33;
        //int TOKEN_DELETE = 34;

        String[] PROJECTION = {Table.ANSWER.getName() + "." + Columns._ID.getName(),
                Table.ANSWER.getName() + "." + Columns.ID.getName(),
                Table.ANSWER.getName() + "." + Columns.QUESTION_ID.getName(),
                Table.ANSWER.getName() + "." + Columns.TASK_ID.getName(),
                Table.ANSWER.getName() + "." + Columns.ANSWER.getName(),
                Table.ANSWER.getName() + "." + Columns.VALUE.getName(),
                Table.ANSWER.getName() + "." + Columns.ROUTING.getName(),
                Table.ANSWER.getName() + "." + Columns.CHECKED.getName(),
                Table.ANSWER.getName() + "." + Columns.FILE_URI.getName(),
                Table.ANSWER.getName() + "." + Columns.FILE_SIZE_B.getName(),
                Table.ANSWER.getName() + "." + Columns.FILE_NAME.getName()
        };

        int _ID = 0;
        int ID = 1;
        int QUESTION_ID = 2;
        int TASK_ID = 3;
        int ANSWER = 4;
        int VALUE = 5;
        int ROUTING = 6;
        int CHECKED = 7;
        int FILE_URI = 8;
        int FILE_SIZE_B = 9;
        int FILE_NAME = 10;
    }
}
