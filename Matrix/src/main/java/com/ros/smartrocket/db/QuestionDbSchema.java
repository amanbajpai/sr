package com.ros.smartrocket.db;

import android.net.Uri;

public interface QuestionDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ", " + Columns.WAVE_ID.getName() + ", "
            + "" + Columns.TASK_ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.QUESTION
            .getName()).build();

    String SORT_ORDER_DESC = Table.QUESTION.getName() + "." + Columns._ID.getName() + " DESC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        WAVE_ID("WaveId", DBType.NUMERIC),
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
        VALIDATION_COMMENT("ValidationComment", DBType.TEXT),
        PRESENT_VALIDATION_TEXT("PresetValidationText", DBType.TEXT),

        MIN_VALUES("MinValue", DBType.NUMERIC),
        MAX_VALUES("MaxValue", DBType.NUMERIC),
        PATTERN_TYPE("PatternType", DBType.NUMERIC),
        VIDEO_SOURCE("VideoSource", DBType.NUMERIC),
        PHOTO_SOURCE("PhotoSource", DBType.NUMERIC),
        VIDEO_URL("VideoUrl", DBType.TEXT),
        PHOTO_URL("PhotoUrl", DBType.TEXT),

        ROUTING("Routing", DBType.NUMERIC),

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
        int TOKEN_QUERY = 21;
        //int TOKEN_INSERT = 22;
        //int TOKEN_UPDATE = 23;
        //int TOKEN_DELETE = 24;

        String[] PROJECTION = {Table.QUESTION.getName() + "." + Columns._ID.getName(),
                Table.QUESTION.getName() + "." + Columns.ID.getName(),
                Table.QUESTION.getName() + "." + Columns.WAVE_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.TASK_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.QUESTION.getName(),
                Table.QUESTION.getName() + "." + Columns.TYPE.getName(),
                Table.QUESTION.getName() + "." + Columns.ORDER_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.MAXIMUM_CHARACTERS.getName(),
                Table.QUESTION.getName() + "." + Columns.MAXIMUM_PHOTOS.getName(),
                Table.QUESTION.getName() + "." + Columns.SHOW_BACK_BUTTON.getName(),
                Table.QUESTION.getName() + "." + Columns.ALLOW_MULTIPLY_PHOTOS.getName(),
                Table.QUESTION.getName() + "." + Columns.ASK_IF.getName(),
                Table.QUESTION.getName() + "." + Columns.PREVIOUS_QUESTION_ORDER_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.VALIDATION_COMMENT.getName(),
                Table.QUESTION.getName() + "." + Columns.PRESENT_VALIDATION_TEXT.getName(),

                Table.QUESTION.getName() + "." + Columns.MIN_VALUES.getName(),
                Table.QUESTION.getName() + "." + Columns.MAX_VALUES.getName(),
                Table.QUESTION.getName() + "." + Columns.PATTERN_TYPE.getName(),
                Table.QUESTION.getName() + "." + Columns.VIDEO_SOURCE.getName(),
                Table.QUESTION.getName() + "." + Columns.PHOTO_SOURCE.getName(),
                Table.QUESTION.getName() + "." + Columns.VIDEO_URL.getName(),
                Table.QUESTION.getName() + "." + Columns.PHOTO_URL.getName(),

                Table.QUESTION.getName() + "." + Columns.ROUTING.getName()


        };

        int _ID = 0;
        int ID = 1;
        int WAVE_ID = 2;
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
        int VALIDATION_COMMENT = 13;
        int PRESENT_VALIDATION_TEXT = 14;

        int MIN_VALUES = 15;
        int MAX_VALUES = 16;
        int PATTERN_TYPE = 17;
        int VIDEO_SOURCE = 18;
        int PHOTO_SOURCE = 19;
        int VIDEO_URL = 20;
        int PHOTO_URL = 21;

        int ROUTING = 22;
    }
}
