package com.ros.smartrocket.db;

import android.net.Uri;

public interface TaskDbSchema {

    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.TASK
            .getName()).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.TASK.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_ASC_LIMIT_1 = Table.TASK.getName() + "." + Columns._ID.getName() + " ASC LIMIT 1";
    String SORT_ORDER_DESC = Table.TASK.getName() + "." + Columns._ID.getName() + " DESC";
    String SORT_ORDER_DESC_MY_TASKS_LIST =
            "CASE " + Table.TASK.getName() + "." + Columns.STATUS_ID.getName()
                    + " WHEN 4 THEN 1 "
                    + "WHEN 2 THEN 2 "
                    + "WHEN 1 THEN 3 "
                    + "WHEN 8 THEN 4 "
                    + "WHEN 5 THEN 5 "
                    + "WHEN 3 THEN 6 "
                    + "WHEN 6 THEN 7 "
                    + "WHEN 7 THEN 8 "
                    + "WHEN 0 THEN 9 "
                    + "ELSE 10 END,  "
                    + Table.TASK.getName() + "." + Columns.LONG_END_DATE_TIME.getName() + " DESC";
    String SORT_ORDER_END_DATE_ASC = Table.TASK.getName() + "." + Columns.LONG_END_DATE_TIME.getName() + " ASC";

    enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        MISSION_ID("missionId", DBType.NUMERIC),
        WAVE_ID("waveId", DBType.NUMERIC),
        USER_ID("userId", DBType.NUMERIC),
        NAME("name", DBType.TEXT),
        DESCRIPTION("description", DBType.TEXT),
        LONGITUDE("longitude", DBType.TEXT),
        LATITUDE("latitude", DBType.TEXT),
        LANGUAGE("language", DBType.TEXT),
        PRICE("price", DBType.FLOAT),
        ADDRESS("address", DBType.TEXT),
        DISTANCE("distance", DBType.FLOAT),
        REMAKE_TILL("remakeTill", DBType.TEXT),
        STARTED("started", DBType.TEXT),
        STATUS_ID("statusId", DBType.INT),
        STATUS("status", DBType.TEXT),
        START_DATE_TIME("startDateTime", DBType.TEXT),
        END_DATE_TIME("endDateTime", DBType.TEXT),
        EXPIRE_DATE_TIME("expireDateTime", DBType.TEXT),
        IS_MY("isMy", DBType.INT),
        IS_HIDE("isHide", DBType.INT),
        STARTED_STATUS_SENT("startedStatusSent", DBType.INT),
        LONG_START_DATE_TIME("longStartDateTime", DBType.NUMERIC),
        LONG_END_DATE_TIME("longEndDateTime", DBType.NUMERIC),
        LONG_EXPIRE_DATE_TIME("longExpireDateTime", DBType.NUMERIC),
        EXPERIENCE_OFFER("experienceOffer", DBType.FLOAT),

        LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK("longExpireTimeoutForClaimedTask", DBType.NUMERIC),
        LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START("longPreClaimedTaskExpireAfterStart", DBType.NUMERIC),
        CLAIMED("claimed", DBType.TEXT),
        REDO_DATE("redoDate", DBType.TEXT),
        APPROVED_AT("approvedAt", DBType.TEXT),
        REJECTED_AT("rejectedAt", DBType.TEXT),
        SUBMITTED_AT("submittedAt", DBType.TEXT),

        COUNTRY_NAME("countryName", DBType.TEXT),

        LONG_REDO_DATE_TIME("longRedoDateTime", DBType.NUMERIC),
        LONG_CLAIM_DATE_TIME("longClaimDateTime", DBType.NUMERIC),

        CURRENCY_SIGN("currencySign", DBType.TEXT),
        LOCATION_NAME("locationName", DBType.TEXT),

        ICON("icon", DBType.TEXT),

        LATITUDE_TO_VALIDATION("latitudeToValidation", DBType.TEXT),
        LONGITUDE_TO_VALIDATION("longitudeToValidation", DBType.TEXT),

        APPROX_MISSION_DURATION("approxMissionDuration", DBType.INT),

        DELETED("deleted", DBType.INT),

        FEEDBACK_SHORT("feedBackShort", DBType.TEXT),
        FEEDBACK_COMMENT("feedBackComment", DBType.TEXT),
        FEEDBACK_COMMENT_FORMATTED("feedBackCommentFormatted", DBType.TEXT);


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
            return columnName;
        }
    }

    interface Query {
        interface All {
            int TOKEN_QUERY = 1;
            int TOKEN_UPDATE = 3;

            String[] PROJECTION = {Table.TASK.getName() + "." + Columns._ID.getName(),
                    Table.TASK.getName() + "." + Columns.ID.getName(),
                    Table.TASK.getName() + "." + Columns.MISSION_ID.getName(),
                    Table.TASK.getName() + "." + Columns.WAVE_ID.getName(),
                    Table.TASK.getName() + "." + Columns.USER_ID.getName(),
                    Table.TASK.getName() + "." + Columns.NAME.getName(),
                    Table.TASK.getName() + "." + Columns.ICON.getName(),
                    Table.TASK.getName() + "." + Columns.DESCRIPTION.getName(),
                    Table.TASK.getName() + "." + Columns.LONGITUDE.getName(),
                    Table.TASK.getName() + "." + Columns.LATITUDE.getName(),
                    Table.TASK.getName() + "." + Columns.LANGUAGE.getName(),
                    Table.TASK.getName() + "." + Columns.ADDRESS.getName(),
                    Table.TASK.getName() + "." + Columns.DISTANCE.getName(),
                    Table.TASK.getName() + "." + Columns.EXPERIENCE_OFFER.getName(),

                    Table.TASK.getName() + "." + Columns.PRICE.getName(),
                    Table.TASK.getName() + "." + Columns.CURRENCY_SIGN.getName(),

                    Table.TASK.getName() + "." + Columns.STATUS_ID.getName(),
                    Table.TASK.getName() + "." + Columns.STATUS.getName(),

                    Table.TASK.getName() + "." + Columns.IS_MY.getName(),
                    Table.TASK.getName() + "." + Columns.IS_HIDE.getName(),
                    Table.TASK.getName() + "." + Columns.STARTED_STATUS_SENT.getName(),

                    Table.TASK.getName() + "." + Columns.STARTED.getName(),
                    Table.TASK.getName() + "." + Columns.REMAKE_TILL.getName(),

                    Table.TASK.getName() + "." + Columns.START_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.END_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.EXPIRE_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.CLAIMED.getName(),
                    Table.TASK.getName() + "." + Columns.REDO_DATE.getName(),

                    Table.TASK.getName() + "." + Columns.LONG_START_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.LONG_END_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.LONG_EXPIRE_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.LONG_CLAIM_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.LONG_REDO_DATE_TIME.getName(),

                    Table.TASK.getName() + "." + Columns.LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK.getName(),
                    Table.TASK.getName() + "." + Columns.LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START.getName(),

                    Table.TASK.getName() + "." + Columns.APPROVED_AT.getName(),
                    Table.TASK.getName() + "." + Columns.REJECTED_AT.getName(),
                    Table.TASK.getName() + "." + Columns.SUBMITTED_AT.getName(),

                    Table.TASK.getName() + "." + Columns.COUNTRY_NAME.getName(),
                    Table.TASK.getName() + "." + Columns.LOCATION_NAME.getName(),

                    Table.TASK.getName() + "." + Columns.LATITUDE_TO_VALIDATION.getName(),
                    Table.TASK.getName() + "." + Columns.LONGITUDE_TO_VALIDATION.getName(),

                    Table.TASK.getName() + "." + Columns.APPROX_MISSION_DURATION.getName(),
                    Table.TASK.getName() + "." + Columns.FEEDBACK_SHORT.getName(),
                    Table.TASK.getName() + "." + Columns.FEEDBACK_COMMENT.getName(),
                    Table.TASK.getName() + "." + Columns.FEEDBACK_COMMENT_FORMATTED.getName()
            };

            int _ID = 0;
            int ID = 1;
            int MISSION_ID = 2;
            int WAVE_ID = 3;
            int USER_ID = 4;
            int NAME = 5;
            int ICON = 6;
            int DESCRIPTION = 7;
            int LONGITUDE = 8;
            int LATITUDE = 9;
            int LANGUAGE = 10;
            int ADDRESS = 11;
            int DISTANCE = 12;
            int EXPERIENCE_OFFER = 13;

            int PRICE = 14;
            int CURRENCY_SIGN = 15;

            int STATUS_ID = 16;
            int STATUS = 17;

            int IS_MY = 18;
            int IS_HIDE = 19;
            int STARTED_STATUS_SENT = 20;

            int STARTED = 21;
            int REMAKE_TILL = 22;

            int START_DATE_TIME = 23;
            int END_DATE_TIME = 24;
            int EXPIRE_DATE_TIME = 25;
            int CLAIMED = 26;
            int REDO_DATE = 27;

            int LONG_START_DATE_TIME = 28;
            int LONG_END_DATE_TIME = 29;
            int LONG_EXPIRE_DATE_TIME = 30;
            int LONG_CLAIM_DATE_TIME = 31;
            int LONG_REDO_DATE_TIME = 32;

            int LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 33;
            int LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 34;

            int APPROVED_AT = 35;
            int REJECTED_AT = 36;
            int SUBMITTED_AT = 37;

            int COUNTRY_NAME = 38;
            int LOCATION_NAME = 39;

            int LATITUDE_TO_VALIDATION = 40;
            int LONGITUDE_TO_VALIDATION = 41;

            int APPROX_MISSION_DURATION = 42;
            int FEEDBACK_SHORT = 43;
            int FEEDBACK_COMMENT = 44;
            int FEEDBACK_COMMENT_FORMATTED = 45;


        }
    }
}
