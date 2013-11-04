package com.matrix.db;

import android.net.Uri;

public interface TaskDbSchema {
    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table.TASK.getName()).build();

    String SORT_ORDER_DESC_LIMIT_1 = Table.TASK.getName() + "." + Columns._ID.getName() + " DESC LIMIT 1";
    String SORT_ORDER_DESC = Table.TASK.getName() + "." + Columns._ID.getName() + " DESC";
    String SORT_ORDER = Table.TASK.getName() + "." + Columns._ID.getName() + " ASC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.TEXT),
        NAME("name", DBType.TEXT),
        DESCRIPTION("description", DBType.TEXT),

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
            return this.getName();
        }
    }

    public interface Query {
        int TOKEN_QUERY = 1;
        int TOKEN_INSERT = 2;
        int TOKEN_UPDATE = 3;
        int TOKEN_DELETE = 4;

        String[] PROJECTION = {Table.TASK.getName() + "." + Columns._ID.getName(),
                Table.TASK.getName() + "." + Columns.ID.getName(),
                Table.TASK.getName() + "." + Columns.NAME.getName(),
                Table.TASK.getName() + "." + Columns.DESCRIPTION.getName()
        };

        int _ID = 0;
        int ID = 1;
        int NAME = 2;
        int DESCRIPTION = 3;
    }
}