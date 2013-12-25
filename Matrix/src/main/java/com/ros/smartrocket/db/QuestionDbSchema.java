package com.ros.smartrocket.db;

import android.net.Uri;

public interface QuestionDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.QUESTION.getName()).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.QUESTION.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_DESC = Table.QUESTION.getName() + "." + Columns._ID.getName() + " DESC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        SURVEY_ID("SurveyId", DBType.NUMERIC),
        QUESTION("Question", DBType.TEXT),
        TYPE("Type", DBType.NUMERIC),
        ORDER_ID("OrderId", DBType.NUMERIC),
        MAXIMUM_CHARACTERS("MaximumCharacters", DBType.NUMERIC),
        MAXIMUM_PHOTOS("MaximumPhotos", DBType.NUMERIC),
        SHOW_BACK_BUTTON("ShowBackButton", DBType.NUMERIC),
        ALLOW_MULTIPLY_PHOTOS("AllowMultiplyPhotos", DBType.NUMERIC),
        ASK_IF("AskIf", DBType.TEXT),

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
                Table.TASK.getName() + "." + Columns.SURVEY_ID.getName(),
                Table.TASK.getName() + "." + Columns.QUESTION.getName(),
                Table.TASK.getName() + "." + Columns.TYPE.getName(),
                Table.TASK.getName() + "." + Columns.ORDER_ID.getName(),
                Table.TASK.getName() + "." + Columns.MAXIMUM_CHARACTERS.getName(),
                Table.TASK.getName() + "." + Columns.MAXIMUM_PHOTOS.getName(),
                Table.TASK.getName() + "." + Columns.SHOW_BACK_BUTTON.getName(),
                Table.TASK.getName() + "." + Columns.ALLOW_MULTIPLY_PHOTOS.getName(),
                Table.TASK.getName() + "." + Columns.ASK_IF.getName()


        };

        int _ID = 0;
        int ID = 1;
        int SURVEY_ID = 2;
        int QUESTION = 3;
        int TYPE = 4;
        int ORDER_ID = 5;
        int MAXIMUM_CHARACTERS = 6;
        int MAXIMUM_PHOTOS = 7;
        int SHOW_BACK_BUTTON = 8;
        int ALLOW_MULTIPLY_PHOTOS = 9;
        int ASK_IF = 10;
    }
}
