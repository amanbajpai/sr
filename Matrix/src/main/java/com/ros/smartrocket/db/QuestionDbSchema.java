package com.ros.smartrocket.db;

import android.net.Uri;

public interface QuestionDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.PRODUCT_ID.getName() + ", " + Columns.ID.getName() + ", " + Columns.WAVE_ID.getName() + ", "
            + "" + Columns.TASK_ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.QUESTION
            .getName()).build();

    String SORT_ORDER_DESC = Table.QUESTION.getName() + "." + Columns._ID.getName() + " DESC";
    String SORT_ORDER_SUBQUESTIONS = Table.QUESTION.getName() + "." + Columns.ORDER_ID.getName();

    enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        WAVE_ID("waveId", DBType.NUMERIC),
        TASK_ID("taskId", DBType.NUMERIC),
        MISSION_ID("missionId", DBType.NUMERIC),
        QUESTION("question", DBType.TEXT),
        TYPE("type", DBType.NUMERIC),
        ORDER_ID("orderId", DBType.NUMERIC),
        MAXIMUM_CHARACTERS("maximumCharacters", DBType.NUMERIC),
        MAXIMUM_PHOTOS("maximumPhotos", DBType.NUMERIC),
        SHOW_BACK_BUTTON("showBackButton", DBType.NUMERIC),
        ALLOW_MULTIPLY_PHOTOS("allowMultiplyPhotos", DBType.NUMERIC),
        ASK_IF("askIf", DBType.TEXT),
        IMAGES_GALLERY("images_gallery", DBType.TEXT),
        TASK_LOCATION("taskLocation", DBType.TEXT),
        PREVIOUS_QUESTION_ORDER_ID("previousQuestionOrderId", DBType.NUMERIC),
        NEXT_ANSWERED_QUESTION_ID("nextAnsweredQuestionId", DBType.NUMERIC),
        VALIDATION_COMMENT("validationComment", DBType.TEXT),
        PRESENT_VALIDATION_TEXT("presetValidationText", DBType.TEXT),

        MIN_VALUES("minValue", DBType.FLOAT),
        MAX_VALUES("maxValue", DBType.FLOAT),
        PATTERN_TYPE("patternType", DBType.NUMERIC),
        VIDEO_SOURCE("videoSource", DBType.NUMERIC),
        PHOTO_SOURCE("photoSource", DBType.NUMERIC),
        VIDEO_URL("videoUrl", DBType.TEXT),
        PHOTO_URL("photoUrl", DBType.TEXT),

        ROUTING("routing", DBType.NUMERIC),
        INSTRUCTION_FILE_URI("instructionFileUri", DBType.TEXT),

        PARENT_QUESTION_ID("parentQuestionId", DBType.NUMERIC),
        CATEGORIES("categories", DBType.TEXT),
        CUSTOM_FIELD_IMAGE_URL("customFieldImageUrl", DBType.TEXT),
        ACTION("action", DBType.NUMERIC),
        IS_REQUIRED("isRequired", DBType.NUMERIC),
        PRODUCT_ID("productId", DBType.NUMERIC),
        DELETED("deleted", DBType.INT),
        IS_REDO("isRedo", DBType.NUMERIC),
        IS_COMPRESS("isCompressionphoto", DBType.NUMERIC);

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

    interface Query {
        int TOKEN_QUERY = 21;
        //int TOKEN_INSERT = 22;
        //int TOKEN_UPDATE = 23;
        //int TOKEN_DELETE = 24;

        String[] PROJECTION = {
                Table.QUESTION.getName() + "." + Columns._ID.getName(),
                Table.QUESTION.getName() + "." + Columns.ID.getName(),
                Table.QUESTION.getName() + "." + Columns.WAVE_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.TASK_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.MISSION_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.QUESTION.getName(),
                Table.QUESTION.getName() + "." + Columns.TYPE.getName(),
                Table.QUESTION.getName() + "." + Columns.ORDER_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.MAXIMUM_CHARACTERS.getName(),
                Table.QUESTION.getName() + "." + Columns.MAXIMUM_PHOTOS.getName(),
                Table.QUESTION.getName() + "." + Columns.SHOW_BACK_BUTTON.getName(),
                Table.QUESTION.getName() + "." + Columns.ALLOW_MULTIPLY_PHOTOS.getName(),
                Table.QUESTION.getName() + "." + Columns.ASK_IF.getName(),
                Table.QUESTION.getName() + "." + Columns.IMAGES_GALLERY.getName(),
                Table.QUESTION.getName() + "." + Columns.TASK_LOCATION.getName(),
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

                Table.QUESTION.getName() + "." + Columns.ROUTING.getName(),
                Table.QUESTION.getName() + "." + Columns.INSTRUCTION_FILE_URI.getName(),
                Table.QUESTION.getName() + "." + Columns.NEXT_ANSWERED_QUESTION_ID.getName(),

                Table.QUESTION.getName() + "." + Columns.PARENT_QUESTION_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.CATEGORIES.getName(),
                Table.QUESTION.getName() + "." + Columns.CUSTOM_FIELD_IMAGE_URL.getName(),
                Table.QUESTION.getName() + "." + Columns.ACTION.getName(),
                Table.QUESTION.getName() + "." + Columns.IS_REQUIRED.getName(),
                Table.QUESTION.getName() + "." + Columns.PRODUCT_ID.getName(),
                Table.QUESTION.getName() + "." + Columns.IS_REDO.getName(),
                Table.QUESTION.getName() + "." + Columns.IS_COMPRESS.getName(),
        };

        int _ID = 0;
        int ID = 1;
        int WAVE_ID = 2;
        int TASK_ID = 3;
        int MISSION_ID = 4;
        int QUESTION = 5;
        int TYPE = 6;
        int ORDER_ID = 7;
        int MAXIMUM_CHARACTERS = 8;
        int MAXIMUM_PHOTOS = 9;
        int SHOW_BACK_BUTTON = 10;
        int ALLOW_MULTIPLY_PHOTOS = 11;
        int ASK_IF = 12;
        int TASK_LOCATION = 13;
        int PREVIOUS_QUESTION_ORDER_ID = 14;
        int VALIDATION_COMMENT = 15;
        int PRESENT_VALIDATION_TEXT = 16;

        int MIN_VALUES = 17;
        int MAX_VALUES = 18;
        int PATTERN_TYPE = 19;
        int VIDEO_SOURCE = 20;
        int PHOTO_SOURCE = 21;
        int VIDEO_URL = 22;
        int PHOTO_URL = 23;

        int ROUTING = 24;
        int INSTRUCTION_FILE_URI = 25;
        int NEXT_ANSWERED_QUESTION_ID = 26;

        int PARENT_QUESTION_ID = 27;
        int CATEGORIES = 28;
        int ACTION = 29;
        int IS_REQUIRED = 30;

        int PRODUCT_ID = 31;
        int IS_REDO = 32;
        int IS_COMPRESS =33;
        int CUSTOM_FIELD_IMAGE_URL = 34;
    }
}
