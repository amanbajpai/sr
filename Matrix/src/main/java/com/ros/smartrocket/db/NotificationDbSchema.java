package com.ros.smartrocket.db;

import android.net.Uri;

/**
 * Created by macbook on 09.10.15.
 */
public interface NotificationDbSchema {

    String CUSTOM_SQL = ", UNIQUE (" + Columns.ID.getName() + ") ON CONFLICT REPLACE";
    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath("entity").appendPath(Table
            .NOTIFICATION.getName()).build();

    String SORT_ORDER_ASC_LIMIT_1 = Table.NOTIFICATION.getName() + "." + Columns._ID.getName() + " ASC LIMIT 1";
    String SORT_ORDER_DESC = Table.NOTIFICATION.getName() + "." + Columns._ID.getName() + " DESC";

    public enum Columns {
        _ID("_id", DBType.PRIMARY),
        ID("id", DBType.NUMERIC),
        MESSAGE("message", DBType.TEXT),
        SUBJECT("subject", DBType.TEXT),
        READ("read", DBType.NUMERIC);

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
            return this.getName();
        }
    }

    interface Query {
        int TOKEN_QUERY = 51;

        String[] PROJECTION = {Table.NOTIFICATION.getName() + "." + Columns._ID.getName(),
                Table.NOTIFICATION.getName() + "." + Columns.ID.getName(),
                Table.NOTIFICATION.getName() + "." + Columns.ID.getName(),
                Table.NOTIFICATION.getName() + "." + Columns.MESSAGE.getName(),
                Table.NOTIFICATION.getName() + "." + Columns.SUBJECT.getName(),
                Table.NOTIFICATION.getName() + "." + Columns.READ.getName(),
        };

        int _ID = 0;
        int ID = 1;
        int MESSAGE = 2;
        int SUBJECT = 3;
        int READ = 4;
    }

}
