package com.matrix.db;

import android.net.Uri;

public interface SurveyDbSchema {
    public static final int SURVEY_BY_DISTANCE = 102;
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.SURVEY.getName()).build();
    Uri CONTENT_URI_SURVEY_BY_DISTANCE = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity")
            .appendPath(Table.SURVEY.getName() + SURVEY_BY_DISTANCE).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.SURVEY.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_DESC = Table.SURVEY.getName() + "." + Columns._ID.getName() + " DESC";
    //String SORT_ORDER = Table.SURVEY.getName() + "." + Columns._ID.getName() + " ASC";

    //final String TASK_COUNT = "task_count";

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
        END_DATE_TIME("EndDateTime", DBType.INT),
        EXPECTED_END_DATE_TIME("ExpectedEndDateTime", DBType.INT),
        EXPECTED_START_DATE_TIME("ExpectedStartDateTime", DBType.INT),

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
            return this.getName();
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
                Table.SURVEY.getName() + "." + Columns.EXPECTED_START_DATE_TIME.getName()
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
    }

    public interface QuerySurveyByDistance {
        int TOKEN_QUERY = 10;

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

                "MIN(" + Table.TASK.getName() + "." + TaskDbSchema.Columns.DISTANCE.getName() + ")",
                "(SELECT COUNT(*) FROM " + Table.TASK.getName() + " WHERE "
                        + Table.TASK.getName() + "." + TaskDbSchema.Columns.SURVEY_ID.getName() + " = " + Table.SURVEY.getName() + "."
                        + Columns.ID.getName() + ")",
                Table.TASK.getName() + "." + TaskDbSchema.Columns.PRICE.getName()
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
        int DISTANCE_TO_NEAR = 18;
        int TASK_COUNT = 19;
        int PRICE = 20;
    }
}
