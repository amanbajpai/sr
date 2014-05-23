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
                    + Table.TASK.getName() + "." + Columns.END_DATE_TIME.getName() + " DESC";
    /*String SORT_ORDER_DESC_MY_TASKS_LIST = Table.TASK.getName() + "." + Columns.STATUS_ID.getName() + " DESC,  " +
            "" + Table.TASK.getName() + "." + Columns.END_DATE_TIME.getName() + " DESC";*/
    String SORT_ORDER_DISTANCE_ASC = Table.TASK.getName() + "." + Columns.DISTANCE.getName() + " ASC";
    String SORT_ORDER_END_DATE_ASC = Table.TASK.getName() + "." + Columns.LONG_END_DATE_TIME.getName() + " ASC";
    //String SORT_ORDER = Table.TASK.getName() + "." + Columns._ID.getName() + " ASC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        SURVEY_ID("SurveyId", DBType.NUMERIC),
        USER_ID("UserId", DBType.NUMERIC),
        NAME("Name", DBType.TEXT),
        DESCRIPTION("Description", DBType.TEXT),
        LONGITUDE("Longitude", DBType.FLOAT),
        LATITUDE("Latitude", DBType.FLOAT),
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
        IS_MY("IsMy", DBType.INT),
        IS_HIDE("IsHide", DBType.INT),
        LONG_END_DATE_TIME("LongEndDateTime", DBType.NUMERIC),
        EXPERIENCE_OFFER("ExperienceOffer", DBType.FLOAT),

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
            int TOKEN_INSERT = 2;
            int TOKEN_UPDATE = 3;
            //int TOKEN_DELETE = 4;

            String[] PROJECTION = {Table.TASK.getName() + "." + Columns._ID.getName(),
                    Table.TASK.getName() + "." + Columns.ID.getName(),
                    Table.TASK.getName() + "." + Columns.SURVEY_ID.getName(),
                    Table.TASK.getName() + "." + Columns.USER_ID.getName(),
                    Table.TASK.getName() + "." + Columns.NAME.getName(),
                    Table.TASK.getName() + "." + Columns.DESCRIPTION.getName(),
                    Table.TASK.getName() + "." + Columns.LONGITUDE.getName(),
                    Table.TASK.getName() + "." + Columns.LATITUDE.getName(),
                    Table.TASK.getName() + "." + Columns.LANGUAGE.getName(),
                    Table.TASK.getName() + "." + Columns.PRICE.getName(),
                    Table.TASK.getName() + "." + Columns.ADDRESS.getName(),
                    Table.TASK.getName() + "." + Columns.DISTANCE.getName(),
                    Table.TASK.getName() + "." + Columns.REMAKE_TILL.getName(),
                    Table.TASK.getName() + "." + Columns.STARTED.getName(),
                    Table.TASK.getName() + "." + Columns.STATUS_ID.getName(),
                    Table.TASK.getName() + "." + Columns.STATUS.getName(),
                    Table.TASK.getName() + "." + Columns.START_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.END_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.IS_MY.getName(),
                    Table.TASK.getName() + "." + Columns.IS_HIDE.getName(),
                    Table.TASK.getName() + "." + Columns.LONG_END_DATE_TIME.getName(),
                    Table.TASK.getName() + "." + Columns.EXPERIENCE_OFFER.getName()
            };

            int _ID = 0;
            int ID = 1;
            int SURVEY_ID = 2;
            int USER_ID = 3;
            int NAME = 4;
            int DESCRIPTION = 5;
            int LONGITUDE = 6;
            int LATITUDE = 7;
            int LANGUAGE = 8;
            int PRICE = 9;
            int ADDRESS = 10;
            int DISTANCE = 11;
            int REMAKE_TILL = 12;
            int STARTED = 13;
            int STATUS_ID = 14;
            int STATUS = 15;
            int START_DATE_TIME = 16;
            int END_DATE_TIME = 17;
            int IS_MY = 18;
            int IS_HIDE = 19;
            int LONG_END_DATE_TIME = 20;
            int EXPERIENCE_OFFER = 21;
        }

        public interface GetDistance {
            int TOKEN_QUERY = 1;
            String[] PROJECTION = {
                    Table.TASK.getName() + "." + Columns.LONGITUDE.getName(),
                    Table.TASK.getName() + "." + Columns.LATITUDE.getName(),
                    Table.TASK.getName() + "." + Columns.DISTANCE.getName()
            };

            int LONGITUDE = 0;
            int LATITUDE = 1;
            int DISTANCE = 2;
        }
    }
}
