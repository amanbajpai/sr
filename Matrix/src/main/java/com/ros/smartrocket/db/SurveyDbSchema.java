package com.ros.smartrocket.db;

import android.net.Uri;

public interface SurveyDbSchema {
    public static final int SURVEY_BY_DISTANCE = 102;
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.SURVEY
            .getName()).build();
    Uri CONTENT_URI_SURVEY_BY_DISTANCE = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity")
            .appendPath(Table.SURVEY.getName() + SURVEY_BY_DISTANCE).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.SURVEY.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    //String SORT_ORDER_DESC = Table.SURVEY.getName() + "." + Columns._ID.getName() + " DESC";
    //String SORT_ORDER = Table.SURVEY.getName() + "." + Columns._ID.getName() + " ASC";

    //final String NEAR_TASK_DISTANCE = "near_task_distance";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("Id", DBType.NUMERIC),
        NAME("Name", DBType.TEXT),
        DESCRIPTION("Description", DBType.TEXT),
        LONGITUDE("Longitude", DBType.FLOAT),
        LATITUDE("Latitude", DBType.FLOAT),
        CLAIMABLE_BEFORE_LIVE("ClaimableBeforeLive", DBType.INT),
        VIEWABLE_BEFORE_LIVE("ViewableBeforeLive", DBType.INT),
        CONCURRENT_CLAIMS_PER_AGENT("ConcurrentClaimsPerAgent", DBType.TEXT),
        EXTERNAL_ID("ExternalId", DBType.TEXT),
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
        PRE_CLAIMED_TASK_EXPIRE_AFTER_START("PreClaimedTaskExpireAfterStart", DBType.INT),
        CLAIMED("Claimed", DBType.TEXT),

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
        int TOKEN_INSERT = 11;
        //int TOKEN_UPDATE = 12;
        //int TOKEN_DELETE = 13;

        String[] PROJECTION = {Table.SURVEY.getName() + "." + Columns._ID.getName(),
                Table.SURVEY.getName() + "." + Columns.ID.getName(),
                Table.SURVEY.getName() + "." + Columns.NAME.getName(),
                Table.SURVEY.getName() + "." + Columns.DESCRIPTION.getName(),
                Table.SURVEY.getName() + "." + Columns.LONGITUDE.getName(),
                Table.SURVEY.getName() + "." + Columns.LATITUDE.getName(),

                Table.SURVEY.getName() + "." + Columns.CLAIMABLE_BEFORE_LIVE.getName(),
                Table.SURVEY.getName() + "." + Columns.VIEWABLE_BEFORE_LIVE.getName(),
                Table.SURVEY.getName() + "." + Columns.CONCURRENT_CLAIMS_PER_AGENT.getName(),
                Table.SURVEY.getName() + "." + Columns.EXTERNAL_ID.getName(),
                Table.SURVEY.getName() + "." + Columns.START_DATE_TIME.getName(),
                Table.SURVEY.getName() + "." + Columns.SUSPENSION_TARGET.getName(),
                Table.SURVEY.getName() + "." + Columns.TARGET_MAXIMUM.getName(),
                Table.SURVEY.getName() + "." + Columns.TARGET_MINIMUM.getName(),
                Table.SURVEY.getName() + "." + Columns.MAXIMUM_CLAIMS_PER_AGENT.getName(),

                Table.SURVEY.getName() + "." + Columns.END_DATE_TIME.getName(),
                Table.SURVEY.getName() + "." + Columns.EXPECTED_END_DATE_TIME.getName(),
                Table.SURVEY.getName() + "." + Columns.EXPECTED_START_DATE_TIME.getName(),
                Table.SURVEY.getName() + "." + Columns.EXPERIENCE_OFFER.getName(),

                Table.SURVEY.getName() + "." + Columns.EXPIRE_TIMEOUT_FOR_CLAIMED_TASK.getName(),
                Table.SURVEY.getName() + "." + Columns.PRE_CLAIMED_TASK_EXPIRE_AFTER_START.getName()
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
        int EXTERNAL_ID = 9;
        int START_DATE_TIME = 10;
        int SUSPENSION_TARGET = 11;
        int TARGET_MAXIMUM = 12;
        int TARGET_MINIMUM = 13;
        int MAXIMUM_CLAIMS_PER_AGENT = 14;
        int END_DATE_TIME = 15;
        int EXPECTED_END_DATE_TIME = 16;
        int EXPECTED_START_DATE_TIME = 17;
        int EXPERIENCE_OFFER = 18;
        int EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 19;
        int PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 20;
    }

    public interface QuerySurveyByDistance {
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
        int EXPIRE_TIMEOUT_FOR_CLAIMED_TASK = 23;
        int PRE_CLAIMED_TASK_EXPIRE_AFTER_START = 24;
    }
}
