package com.ros.smartrocket.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ros.smartrocket.utils.L;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AppSQLiteOpenHelper extends SQLiteOpenHelper {
    public AppSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * * WARNING! Reflection is used here!
     * Every Table must specify name and class of schema
     * <p/>
     * Every schema must have: - CUSTOM_SQL constant for specific logic while creating tables - Columns enumeration
     * with getName() and getType() methods getName() must return String getType() must return DBType
     *
     * @param sql
     * @param table
     * @return
     */
    protected boolean appendCreateTableSQL(StringBuilder sql, Table table) {
        SchemeInterpretator schemeInterpretator = new SchemeInterpretator(table.getSchema());
        sql.append("CREATE TABLE ").append(table.getName()).append(" (");
        StringBuilder columnsSql = new StringBuilder(1024);

        HashMap<String, String> columns = schemeInterpretator.getColumnsMap();
        Iterator<Map.Entry<String, String>> it = columns.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = it.next();
            if (columnsSql.length() > 0) {
                columnsSql.append(",");
            }
            columnsSql.append(pairs.getKey()).append(" ").append(pairs.getValue());
        }

        if (columnsSql.length() > 0) {
            sql.append(columnsSql.toString());
        } else {
            return true;
        }

        String customSql = schemeInterpretator.getCustomSql();
        if (customSql != null && customSql.length() > 0) {
            sql.append(customSql);
        }
        sql.append(")");

        L.d(getClass().getSimpleName(), sql.toString());
        return false;
    }

    public static String preparePlaceHolders(int count) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < count; i++) {
            result.append(i > 0 ? ",?" : "?");
        }
        return result.toString();
    }
}
