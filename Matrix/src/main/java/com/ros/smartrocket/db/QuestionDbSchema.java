package com.ros.smartrocket.db;

import android.net.Uri;

public interface QuestionDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.QUESTION
            .getName()).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.QUESTION.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_DESC = Table.QUESTION.getName() + "." + Columns._ID.getName() + " DESC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        SURVEY_ID("SurveyId", DBType.NUMERIC),
        TASK_ID("TaskId", DBType.NUMERIC),
        QUESTION("Question", DBType.TEXT),
        TYPE("Type", DBType.NUMERIC),
        ORDER_ID("OrderId", DBType.NUMERIC),
        MAXIMUM_CHARACTERS("MaximumCharacters", DBType.NUMERIC),
        MAXIMUM_PHOTOS("MaximumPhotos", DBType.NUMERIC),
        SHOW_BACK_BUTTON("ShowBackButton", DBType.NUMERIC),
        ALLOW_MULTIPLY_PHOTOS("AllowMultiplyPhotos", DBType.NUMERIC),
        ASK_IF("AskIf", DBType.TEXT),
        PREVIOUS_QUESTION_ORDER_ID("PreviousQuestionOrderId", DBType.NUMERIC),

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
        int TOKEN_QUERY = 21;
        int TOKEN_INSERT = 22;
        int TOKEN_UPDATE = 23;
        //int TOKEN_DELETE = 24;

        String[] PROJECTION = {Table.QUESTION.getName() + "." + Columns._ID.getName(),
                Table.QUESTION.getName() + "." + Columns.ID.getName(),
                Table.QUESTION.getName() + "." + Columns.SURVEY_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.TASK_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.QUESTION.getName(),
                Table.QUESTION.getName() + "." + Columns.TYPE.getName(),
                Table.QUESTION.getName() + "." + Columns.ORDER_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.MAXIMUM_CHARACTERS.getName(),
                Table.QUESTION.getName() + "." + Columns.MAXIMUM_PHOTOS.getName(),
                Table.QUESTION.getName() + "." + Columns.SHOW_BACK_BUTTON.getName(),
                Table.QUESTION.getName() + "." + Columns.ALLOW_MULTIPLY_PHOTOS.getName(),
                Table.QUESTION.getName() + "." + Columns.ASK_IF.getName(),
                Table.QUESTION.getName() + "." + Columns.PREVIOUS_QUESTION_ORDER_ID.getName()


        };

        int _ID = 0;
        int ID = 1;
        int SURVEY_ID = 2;
        int TASK_ID = 3;
        int QUESTION = 4;
        int TYPE = 5;
        int ORDER_ID = 6;
        int MAXIMUM_CHARACTERS = 7;
        int MAXIMUM_PHOTOS = 8;
        int SHOW_BACK_BUTTON = 9;
        int ALLOW_MULTIPLY_PHOTOS = 10;
        int ASK_IF = 11;
        int PREVIOUS_QUESTION_ORDER_ID = 12;
    }
}
