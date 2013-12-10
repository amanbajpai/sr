package com.matrix.db;

import android.net.Uri;

public interface TaskDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.TASK.getName()).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.TASK.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_DESC = Table.TASK.getName() + "." + Columns._ID.getName() + " DESC";
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

        DELETED("deleted", DBType.INT);

        private String columnName;
        private DBType type;

        Columns(String columnName, DBType type) {
            this.columnName = columnName;
            this.type = type;
        }

        /**
         *
         * @return
         */
        public String getName() {
            return columnName;
        }

        /**
         *
         * @return
         */
        public DBType getType() {
            return type;
        }

        /**
         *
         * @return
         */
        public String toString() {
            return this.getName();
        }
    }

    public interface Query {
        public interface All {
            int TOKEN_QUERY = 1;
            int TOKEN_INSERT = 2;
            //int TOKEN_UPDATE = 3;
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
                    Table.TASK.getName() + "." + Columns.DISTANCE.getName()
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
