package com.ros.smartrocket.db;

import android.net.Uri;

public interface AnswerDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.ANSWER.getName
            ()).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.ANSWER.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_DESC = Table.ANSWER.getName() + "." + Columns._ID.getName() + " DESC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        QUESTION_ID("QuestionId", DBType.NUMERIC),
        TEXT("Text", DBType.TEXT),

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
        int TOKEN_QUERY = 1;
        int TOKEN_INSERT = 2;
        int TOKEN_UPDATE = 3;
        //int TOKEN_DELETE = 4;

        String[] PROJECTION = {Table.TASK.getName() + "." + Columns._ID.getName(),
                Table.TASK.getName() + "." + Columns.ID.getName(),
                Table.TASK.getName() + "." + Columns.QUESTION_ID.getName(),
                Table.TASK.getName() + "." + Columns.TEXT.getName()

        };

        int _ID = 0;
        int ID = 1;
        int QUESTION_ID = 2;
        int TEXT = 3;
    }
}
