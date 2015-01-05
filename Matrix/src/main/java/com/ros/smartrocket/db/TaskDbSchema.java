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

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("Id", DBType.NUMERIC),
        WAVE_ID("WaveId", DBType.NUMERIC),
        USER_ID("UserId", DBType.NUMERIC),
        NAME("Name", DBType.TEXT),
        DESCRIPTION("Description", DBType.TEXT),
        LONGITUDE("Longitude", DBType.TEXT),
        LATITUDE("Latitude", DBType.TEXT),
        LANGUAGE("Language", DBType.TEXT),
        PRICE("Price", DBType.FLOAT),
        ADDRESS("Address", DBType.TEXT),
        DISTANCE("Distance", DBType.FLOAT),
        REMAKE_TILL("RemakeTill", DBType.TEXT),
        STARTED("Started", DBType.TEXT),
        STATUS_ID("StatusId", DBType.INT),
        STATUS("Status", DBType.TEXT),
        START_DATE_TIME("StartDateTime", DBType.TEXT),
        END_DATE_TIME("EndDateTime", DBType.TEXT),
        EXPIRE_DATE_TIME("ExpireDateTime", DBType.TEXT),
        IS_MY("IsMy", DBType.INT),
        IS_HIDE("IsHide", DBType.INT),
        STARTED_STATUS_SENT("StartedStatusSent", DBType.INT),
        LONG_START_DATE_TIME("LongStartDateTime", DBType.NUMERIC),
        LONG_END_DATE_TIME("LongEndDateTime", DBType.NUMERIC),
        LONG_EXPIRE_DATE_TIME("LongExpireDateTime", DBType.NUMERIC),
        EXPERIENCE_OFFER("ExperienceOffer", DBType.FLOAT),

        LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK("LongExpireTimeoutForClaimedTask", DBType.NUMERIC),
        LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START("LongPreClaimedTaskExpireAfterStart", DBType.NUMERIC),
        CLAIMED("Claimed", DBType.TEXT),
        REDO_DATE("RedoDate", DBType.TEXT),
        APPROVED_AT("ApprovedAt", DBType.TEXT),
        REJECTED_AT("RejectedAt", DBType.TEXT),
        SUBMITTED_AT("SubmittedAt", DBType.TEXT),

        COUNTRY_NAME("CountryName", DBType.TEXT),

        LONG_REDO_DATE_TIME("LongRedoDateTime", DBType.NUMERIC),
        LONG_CLAIM_DATE_TIME("LongClaimDateTime", DBType.NUMERIC),

        PHOTO_QUESTIONS_COUNT("PhotoQuestionsCount", DBType.INT),
        NO_PHOTO_QUESTIONS_COUNT("NoPhotoQuestionsCount", DBType.INT),

        CURRENCY_SIGN("CurrencySign", DBType.TEXT),
        LOCATION_NAME("LocationName", DBType.TEXT),

        ICON("Icon", DBType.TEXT),

        LATITUDE_TO_VALIDATION("LatitudeToValidation", DBType.TEXT),
        LONGITUDE_TO_VALIDATION("LongitudeToValidation", DBType.TEXT),

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
            return columnName;
        }
    }

    public interface Query {
        public interface All {
            int TOKEN_QUERY = 1;
            int TOKEN_UPDATE = 3;

            String[] PROJECTION = {Table.TASK.getName() + "." + Columns._ID.getName(),
                    Table.TASK.getName() + "." + Columns.ID.getName(),
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
                    Table.TASK.getName() + "." + Columns.PHOTO_QUESTIONS_COUNT.getName(),
                    Table.TASK.getName() + "." + Columns.NO_PHOTO_QUESTIONS_COUNT.getName(),

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
                    Table.TASK.getName() + "." + Columns.LONGITUDE_TO_VALIDATION.getName()
            };

            int _ID = 0;
            int ID = 1;
            int WAVE_ID = 2;
            int USER_ID = 3;
            int NAME = 4;
            int ICON = 5;
            int DESCRIPTION = 6;
            int LONGITUDE = 7;
            int LATITUDE = 8;
            int LANGUAGE = 9;
            int ADDRESS = 10;
            int DISTANCE = 11;
            int EXPERIENCE_OFFER = 12;
            int PHOTO_QUESTIONS_COUNT = 13;
            int NO_PHOTO_QUESTIONS_COUNT = 14;

            int PRICE = 15;
            int CURRENCY_SIGN = 16;

            int STATUS_ID = 17;
            int STATUS = 18;

            int IS_MY = 19;
            int IS_HIDE = 20;
            int STARTED_STATUS_SENT = 21;

            int STARTED = 22;
            int REMAKE_TILL = 23;

            int START_DATE_TIME = 24;
            int END_DATE_TIME = 25;
            int EXPIRE_DATE_TIME = 26;
            int CLAIMED = 27;
            int REDO_DATE = 28;

            int LONG_START_DATE_TIME = 29;
            int LONG_END_DATE_TIME = 30;
            int LONG_EXPIRE_DATE_TIME = 31;
            int LONG_CLAIM_DATE_TIME = 32;
            int LONG_REDO_DATE_TIME = 33;

            int LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 34;
            int LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 35;

            int APPROVED_AT = 36;
            int REJECTED_AT = 37;
            int SUBMITTED_AT = 38;

            int COUNTRY_NAME = 39;
            int LOCATION_NAME = 40;

            int LATITUDE_TO_VALIDATION = 41;
            int LONGITUDE_TO_VALIDATION = 42;
        }
    }
}
