package com.ros.smartrocket.db;

import android.net.Uri;

public interface WaveDbSchema {

    int WAVE_BY_DISTANCE = 102;
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.WAVE
            .getName()).build();
    Uri CONTENT_URI_WAVE_BY_DISTANCE = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity")
            .appendPath(Table.WAVE.getName() + WAVE_BY_DISTANCE).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.WAVE.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";

    enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        NAME("name", DBType.TEXT),
        DESCRIPTION("description", DBType.TEXT),
        LONGITUDE("longitude", DBType.TEXT),
        LATITUDE("latitude", DBType.TEXT),
        CAN_BE_PRE_CLAIMED("isCanBePreClaimed", DBType.INT),
        START_DATE_TIME("startDateTime", DBType.TEXT),
        END_DATE_TIME("endDateTime", DBType.TEXT),
        EXPERIENCE_OFFER("experienceOffer", DBType.FLOAT),
        EXPECTED_START_DATE_TIME("expectedStartDateTime", DBType.TEXT),

        EXPIRE_TIMEOUT_FOR_CLAIMED_TASK("expireTimeoutForClaimedTask", DBType.INT),
        LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK("longExpireTimeoutForClaimedTask", DBType.NUMERIC),
        PRE_CLAIMED_TASK_EXPIRE_AFTER_START("preClaimedTaskExpireAfterStart", DBType.INT),
        LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START("longPreClaimedTaskExpireAfterStart", DBType.NUMERIC),
        CLAIMED("claimed", DBType.TEXT),

        ICON("icon", DBType.TEXT),

        LONG_START_DATE_TIME("longStartDateTime", DBType.NUMERIC),
        DOWNLOAD_MEDIA_WHEN_CLAIMING_TASK("downloadMediaWhenClaimingTask", DBType.INT),

        CONTAINS_DIFFERENT_RATE("containsDifferentRate", DBType.INT),
        RATE("rate", DBType.FLOAT),

        ID_CARD_STATUS("idCardStatus", DBType.INT),
        ID_CARD_LOGO("idCardLogo", DBType.TEXT),
        ID_CARD_TEXT("idCardText", DBType.TEXT),

        APPROX_MISSION_DURATION("approxMissionDuration", DBType.INT),
        MISSION_SIZE("missionSize", DBType.INT),

        DELETED("deleted", DBType.INT);

        private String columnName;
        private DBType type;

        Columns(String columnName, DBType type) {
            this.columnName = columnName;
            this.type = type;
        }

        public String getName() {
            return columnName;
        }

        public DBType getType() {
            return type;
        }

        public String toString() {
            return columnName;
        }
    }

    interface Query {
        int TOKEN_QUERY = 10;

        String[] PROJECTION = {
                Table.WAVE.getName() + "." + Columns._ID.getName(),
                Table.WAVE.getName() + "." + Columns.ID.getName(),
                Table.WAVE.getName() + "." + Columns.NAME.getName(),
                Table.WAVE.getName() + "." + Columns.DESCRIPTION.getName(),
                Table.WAVE.getName() + "." + Columns.LONGITUDE.getName(),
                Table.WAVE.getName() + "." + Columns.LATITUDE.getName(),
                Table.WAVE.getName() + "." + Columns.START_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.END_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.EXPERIENCE_OFFER.getName(),
                Table.WAVE.getName() + "." + Columns.LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK.getName(),
                Table.WAVE.getName() + "." + Columns.EXPECTED_START_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.EXPIRE_TIMEOUT_FOR_CLAIMED_TASK.getName(),
                Table.WAVE.getName() + "." + Columns.PRE_CLAIMED_TASK_EXPIRE_AFTER_START.getName(),
                Table.WAVE.getName() + "." + Columns.ICON.getName(),
                Table.WAVE.getName() + "." + Columns.LONG_START_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START.getName(),
                Table.WAVE.getName() + "." + Columns.CAN_BE_PRE_CLAIMED.getName(),
                Table.WAVE.getName() + "." + Columns.DOWNLOAD_MEDIA_WHEN_CLAIMING_TASK.getName(),
                Table.WAVE.getName() + "." + Columns.CONTAINS_DIFFERENT_RATE.getName(),
                Table.WAVE.getName() + "." + Columns.RATE.getName(),
                Table.WAVE.getName() + "." + Columns.ID_CARD_STATUS.getName(),
                Table.WAVE.getName() + "." + Columns.ID_CARD_LOGO.getName(),
                Table.WAVE.getName() + "." + Columns.ID_CARD_TEXT.getName(),
                Table.WAVE.getName() + "." + Columns.APPROX_MISSION_DURATION.getName(),
                Table.WAVE.getName() + "." + Columns.MISSION_SIZE.getName()
        };

        int _ID = 0;
        int ID = 1;
        int NAME = 2;
        int DESCRIPTION = 3;
        int LONGITUDE = 4;
        int LATITUDE = 5;
        int START_DATE_TIME = 6;
        int END_DATE_TIME = 7;
        int EXPERIENCE_OFFER = 8;
        int LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 9;
        int EXPECTED_START_DATE_TIME = 10;
        int EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 11;
        int PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 12;
        int ICON = 13;
        int LONG_START_DATE_TIME = 14;
        int LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 15;
        int CAN_BE_PRE_CLAIMED = 16;
        int DOWNLOAD_MEDIA_WHEN_CLAIMING_TASK = 17;
        int CONTAINS_DIFFERENT_RATE = 18;
        int RATE = 19;
        int ID_CARD_STATUS = 20;
        int ID_CARD_LOGO = 21;
        int ID_CARD_TEXT = 22;
        int APPROX_MISSION_DURATION = 23;
        int MISSION_SIZE = 24;
    }

    interface QueryWaveByDistance {
        int TOKEN_QUERY = 10;

        int _ID = 0;
        int ID = 1;
        int NAME = 2;
        int DESCRIPTION = 3;
        int LONGITUDE = 4;
        int LATITUDE = 5;
        int START_DATE_TIME = 6;
        int END_DATE_TIME = 7;
        int EXPECTED_START_DATE_TIME = 8;
        int NEAR_TASK_DISTANCE = 9;
        int TASK_COUNT = 10;
        int NEAR_TASK_PRICE = 11;
        int NEAR_TASK_ID = 12;
        int EXPERIENCE_OFFER = 13;
        int LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 14;
        int EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 15;
        int PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 16;
        int IS_ALL_TASK_HIDE = 17;
        int NEAR_TASK_CURRENCY_SIGN = 18;
        int ICON = 19;
        int LONG_START_DATE_TIME = 20;
        int LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 21;
        int CAN_BE_PRE_CLAIMED = 22;
        int DOWNLOAD_MEDIA_WHEN_CLAIMING_TASK = 23;
        int CONTAINS_DIFFERENT_RATE = 24;
        int RATE = 25;
        int APPROX_MISSION_DURATION = 26;
        int MISSION_SIZE = 27;
    }
}
