package com.matrix.db;

import android.net.Uri;

public interface SurveyDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.SURVEY.getName()).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.SURVEY.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_DESC = Table.SURVEY.getName() + "." + Columns._ID.getName() + " DESC";
    String SORT_ORDER = Table.SURVEY.getName() + "." + Columns._ID.getName() + " ASC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("Id", DBType.NUMERIC),
        USER_ID("UserId", DBType.NUMERIC),
        NAME("Name", DBType.TEXT),
        DESCRIPTION("Description", DBType.TEXT),
        LONGITUDE("Longitude", DBType.FLOAT),
        LATITUDE("Latitude", DBType.FLOAT),
        LANGUAGE("Language", DBType.TEXT),

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
        int TOKEN_QUERY = 1;
        int TOKEN_INSERT = 2;
        int TOKEN_UPDATE = 3;
        int TOKEN_DELETE = 4;

        String[] PROJECTION = {Table.SURVEY.getName() + "." + Columns._ID.getName(),
                Table.SURVEY.getName() + "." + Columns.ID.getName(),
                Table.SURVEY.getName() + "." + Columns.USER_ID.getName(),
                Table.SURVEY.getName() + "." + Columns.NAME.getName(),
                Table.SURVEY.getName() + "." + Columns.DESCRIPTION.getName(),
                Table.SURVEY.getName() + "." + Columns.LONGITUDE.getName(),
                Table.SURVEY.getName() + "." + Columns.LATITUDE.getName(),
                Table.SURVEY.getName() + "." + Columns.LANGUAGE.getName()
        };

        int _ID = 0;
        int ID = 1;
        int USER_ID = 2;
        int NAME = 3;
        int DESCRIPTION = 4;
        int LONGITUDE = 5;
        int LATITUDE = 6;
        int LANGUAGE = 7;
    }
}