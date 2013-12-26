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
        ANSWER("Answer", DBType.TEXT),
        VALUE("Value", DBType.TEXT),
        ROUTING("Routing", DBType.NUMERIC),
        CHECKED("Checked", DBType.NUMERIC),

        IMAGE_BYTE_ARRAY("imageByteArray", DBType.BLOB),

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
        int TOKEN_QUERY = 31;
        int TOKEN_INSERT = 32;
        int TOKEN_UPDATE = 33;
        //int TOKEN_DELETE = 34;

        String[] PROJECTION = {Table.ANSWER.getName() + "." + Columns._ID.getName(),
                Table.ANSWER.getName() + "." + Columns.ID.getName(),
                Table.ANSWER.getName() + "." + Columns.QUESTION_ID.getName(),
                Table.ANSWER.getName() + "." + Columns.ANSWER.getName(),
                Table.ANSWER.getName() + "." + Columns.VALUE.getName(),
                Table.ANSWER.getName() + "." + Columns.ROUTING.getName(),
                Table.ANSWER.getName() + "." + Columns.CHECKED.getName(),
                Table.ANSWER.getName() + "." + Columns.IMAGE_BYTE_ARRAY.getName()
        };

        int _ID = 0;
        int ID = 1;
        int QUESTION_ID = 2;
        int ANSWER = 3;
        int VALUE = 4;
        int ROUTING = 5;
        int CHECKED = 6;
        int IMAGE_BYTE_ARRAY = 7;
    }
}
