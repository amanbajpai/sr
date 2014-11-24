package com.ros.smartrocket.db;

import android.net.Uri;

public interface WaveDbSchema {
    public static final int WAVE_BY_DISTANCE = 102;
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.WAVE
            .getName()).build();
    Uri CONTENT_URI_WAVE_BY_DISTANCE = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity")
            .appendPath(Table.WAVE.getName() + WAVE_BY_DISTANCE).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.WAVE.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("Id", DBType.NUMERIC),
        NAME("Name", DBType.TEXT),
        DESCRIPTION("Description", DBType.TEXT),
        LONGITUDE("Longitude", DBType.FLOAT),
        LATITUDE("Latitude", DBType.FLOAT),
        CLAIMABLE_BEFORE_LIVE("ClaimableBeforeLive", DBType.INT),
        CAN_BE_PRE_CLAIMED("IsCanBePreClaimed", DBType.INT),
        VIEWABLE_BEFORE_LIVE("ViewableBeforeLive", DBType.INT),
        CONCURRENT_CLAIMS_PER_AGENT("ConcurrentClaimsPerAgent", DBType.TEXT),
        EXTERNAL_WAVE_ID("ExternalWaveId", DBType.TEXT),
        START_DATE_TIME("StartDateTime", DBType.TEXT),
        SUSPENSION_TARGET("SuspensionTarget", DBType.INT),
        TARGET_MAXIMUM("TargetMaximum", DBType.INT),
        TARGET_MINIMUM("TargetMinimum", DBType.INT),
        MAXIMUM_CLAIMS_PER_AGENT("MaximumClaimsPerAgent", DBType.INT),
        END_DATE_TIME("EndDateTime", DBType.TEXT),
        EXPECTED_END_DATE_TIME("ExpectedEndDateTime", DBType.TEXT),
        EXPECTED_START_DATE_TIME("ExpectedStartDateTime", DBType.TEXT),
        EXPERIENCE_OFFER("ExperienceOffer", DBType.FLOAT),

        EXPIRE_TIMEOUT_FOR_CLAIMED_TASK("ExpireTimeoutForClaimedTask", DBType.INT),
        LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK("LongExpireTimeoutForClaimedTask", DBType.NUMERIC),
        PRE_CLAIMED_TASK_EXPIRE_AFTER_START("PreClaimedTaskExpireAfterStart", DBType.INT),
        LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START("LongPreClaimedTaskExpireAfterStart", DBType.NUMERIC),
        CLAIMED("Claimed", DBType.TEXT),

        PHOTO_QUESTIONS_COUNT("PhotoQuestionsCount", DBType.INT),
        NO_PHOTO_QUESTIONS_COUNT("NoPhotoQuestionsCount", DBType.INT),

        ICON("Icon", DBType.TEXT),

        LONG_START_DATE_TIME("LongStartDateTime", DBType.NUMERIC),

        DELETED("Deleted", DBType.INT);

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

    public interface Query {
        int TOKEN_QUERY = 10;

        String[] PROJECTION = {Table.WAVE.getName() + "." + Columns._ID.getName(),
                Table.WAVE.getName() + "." + Columns.ID.getName(),
                Table.WAVE.getName() + "." + Columns.NAME.getName(),
                Table.WAVE.getName() + "." + Columns.DESCRIPTION.getName(),
                Table.WAVE.getName() + "." + Columns.LONGITUDE.getName(),
                Table.WAVE.getName() + "." + Columns.LATITUDE.getName(),

                Table.WAVE.getName() + "." + Columns.CLAIMABLE_BEFORE_LIVE.getName(),
                Table.WAVE.getName() + "." + Columns.VIEWABLE_BEFORE_LIVE.getName(),
                Table.WAVE.getName() + "." + Columns.CONCURRENT_CLAIMS_PER_AGENT.getName(),
                Table.WAVE.getName() + "." + Columns.EXTERNAL_WAVE_ID.getName(),
                Table.WAVE.getName() + "." + Columns.START_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.SUSPENSION_TARGET.getName(),
                Table.WAVE.getName() + "." + Columns.TARGET_MAXIMUM.getName(),
                Table.WAVE.getName() + "." + Columns.TARGET_MINIMUM.getName(),
                Table.WAVE.getName() + "." + Columns.MAXIMUM_CLAIMS_PER_AGENT.getName(),

                Table.WAVE.getName() + "." + Columns.END_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.EXPECTED_END_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.EXPECTED_START_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.EXPERIENCE_OFFER.getName(),

                Table.WAVE.getName() + "." + Columns.LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK.getName(),
                Table.WAVE.getName() + "." + Columns.EXPIRE_TIMEOUT_FOR_CLAIMED_TASK.getName(),
                Table.WAVE.getName() + "." + Columns.PRE_CLAIMED_TASK_EXPIRE_AFTER_START.getName(),

                Table.WAVE.getName() + "." + Columns.PHOTO_QUESTIONS_COUNT.getName(),
                Table.WAVE.getName() + "." + Columns.NO_PHOTO_QUESTIONS_COUNT.getName(),

                Table.WAVE.getName() + "." + Columns.ICON.getName(),

                Table.WAVE.getName() + "." + Columns.LONG_START_DATE_TIME.getName(),
                Table.WAVE.getName() + "." + Columns.LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START.getName(),
                Table.WAVE.getName() + "." + Columns.CAN_BE_PRE_CLAIMED.getName()
        };

        int _ID = 0;
        int ID = 1;
        int NAME = 2;
        int DESCRIPTION = 3;
        int LONGITUDE = 4;
        int LATITUDE = 5;
        int CLAIMABLE_BEFORE_LIVE = 6;
        int VIEWABLE_BEFORE_LIVE = 7;
        int CONCURRENT_CLAIMS_PER_AGENT = 8;
        int EXTERNAL_WAVE_ID = 9;
        int START_DATE_TIME = 10;
        int SUSPENSION_TARGET = 11;
        int TARGET_MAXIMUM = 12;
        int TARGET_MINIMUM = 13;
        int MAXIMUM_CLAIMS_PER_AGENT = 14;
        int END_DATE_TIME = 15;
        int EXPECTED_END_DATE_TIME = 16;
        int EXPECTED_START_DATE_TIME = 17;
        int EXPERIENCE_OFFER = 18;
        int LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 19;
        int EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 20;
        int PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 21;
        int PHOTO_QUESTIONS_COUNT = 22;
        int NO_PHOTO_QUESTIONS_COUNT = 23;
        int ICON = 24;
        int LONG_START_DATE_TIME = 25;
        int LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 26;
        int CAN_BE_PRE_CLAIMED = 27;
    }

    public interface QueryWaveByDistance {
        int TOKEN_QUERY = 10;

        //Look projection in AppContentProvider

        int _ID = 0;
        int ID = 1;
        int NAME = 2;
        int DESCRIPTION = 3;
        int LONGITUDE = 4;
        int LATITUDE = 5;
        int CLAIMABLE_BEFORE_LIVE = 6;
        int VIEWABLE_BEFORE_LIVE = 7;
        int CONCURRENT_CLAIMS_PER_AGENT = 8;
        int EXTERNAL_ID = 9;
        int START_DATE_TIME = 10;
        int SUSPENSION_TARGET = 11;
        int TARGET_MAXIMUM = 12;
        int TARGET_MINIMUM = 13;
        int MAXIMUM_CLAIMS_PER_AGENT = 14;
        int END_DATE_TIME = 15;
        int EXPECTED_END_DATE_TIME = 16;
        int EXPECTED_START_DATE_TIME = 17;
        int NEAR_TASK_DISTANCE = 18;
        int TASK_COUNT = 19;
        int NEAR_TASK_PRICE = 20;
        int NEAR_TASK_ID = 21;
        int EXPERIENCE_OFFER = 22;
        int LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 23;
        int EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 24;
        int PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 25;
        int PHOTO_QUESTIONS_COUNT = 26;
        int NO_PHOTO_QUESTIONS_COUNT = 27;
        int IS_ALL_TASK_HIDE = 28;
        int NEAR_TASK_CURRENCY_SIGN = 29;
        int ICON = 30;
        int LONG_START_DATE_TIME = 31;
        int LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 32;
        int CAN_BE_PRE_CLAIMED = 33;
    }
}
