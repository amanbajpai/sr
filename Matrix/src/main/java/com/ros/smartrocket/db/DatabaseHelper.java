package com.ros.smartrocket.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ros.smartrocket.utils.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends AppSQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final int DB_VERSION = 53;
    private static final String DB_NAME = "matrix_db";
    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder(1024);
        for (Table table : Table.values()) {
            if (appendCreateTableSQL(sql, table)) {
                continue;
            }
            db.execSQL(sql.toString());
            sql.setLength(0);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Table table : Table.values()) {

            db.beginTransaction();
            try {
                if (oldVersion < 25) {
                    db.execSQL("DROP TABLE IF EXISTS " + table.getName());

                    StringBuilder sql = new StringBuilder(1024);
                    if (appendCreateTableSQL(sql, table)) {
                        continue;
                    }
                    db.execSQL(sql.toString());
                    sql.setLength(0);
                } else {
                    List<ContentValues> tableContent = new ArrayList<ContentValues>();

                    SchemeInterpreter schemeInterpretator = new SchemeInterpreter(table.getSchema());
                    HashMap<String, String> columns = schemeInterpretator.getColumnsMap();

                    Cursor c = null;
                    try {
                        c = db.query(table.getName(), null, null, null, null, null, null);
                    } catch (Exception e) {
                        L.e(TAG, "Error process table: " + table.getName()
                                + " Exception text: " + e.getLocalizedMessage(), e);
                    }

                    if (c != null && c.getCount() > 0) {
                        c.moveToFirst();
                        do {
                            ContentValues contentValues = new ContentValues();

                            for (String columnName : c.getColumnNames()) {
                                if (columns.containsKey(columnName)) {

                                    L.e(TAG, "columnType: " + columns.get(columnName));
                                    DBType columnType = DBType.fromName(columns.get(columnName));

                                    if (columnType != null) {
                                        switch (columnType) {
                                            case PRIMARY:
                                            case NUMERIC:
                                            case INT_DEF:
                                            case INT:
                                                contentValues.put(columnName, c.getInt(c.getColumnIndex(columnName)));
                                                break;
                                            case FLOAT:
                                                contentValues.put(columnName, c.getFloat(c.getColumnIndex(columnName)));
                                                break;
                                            case TEXT:
                                                contentValues.put(columnName, c.getString(c.getColumnIndex(columnName)));
                                                break;
                                            case BLOB:
                                                contentValues.put(columnName, c.getBlob(c.getColumnIndex(columnName)));
                                                break;
                                            default:
                                                break;
                                        }
                                    } else {
                                        L.d(TAG, "Field: \"" + columnName + "\" not added to ContentValues");
                                    }

                                }
                            }

                            L.i(TAG, "Save contentValues: " + contentValues.toString());
                            tableContent.add(contentValues);

                        } while (c.moveToNext());
                        c.close();
                    }


                    db.execSQL("DROP TABLE IF EXISTS " + table.getName());

                    StringBuilder sql = new StringBuilder(1024);
                    if (appendCreateTableSQL(sql, table)) {
                        continue;
                    }
                    db.execSQL(sql.toString());
                    sql.setLength(0);

                    for (ContentValues contentValues : tableContent) {
                        L.i(TAG, "Insert contentValues: " + contentValues.toString());
                        db.insert(table.getName(), null, contentValues);
                    }

                    try {

                        if (newVersion > 52) {
                            db.execSQL("ALTER TABLE " + Table.QUESTION.getName() + " ADD COLUMN " + QuestionDbSchema.Columns.IS_COMPRESS.getName() + " " + DBType.NUMERIC + " DEFAULT 0 , "
                            + QuestionDbSchema.Columns.CUSTOM_FIELD_IMAGE_URL +" "+ DBType.TEXT+" , "+ QuestionDbSchema.Columns.IMAGES_GALLERY+" "
                                    + DBType.TEXT);

                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }

                db.setTransactionSuccessful();
            } catch (Exception e) {
                L.e(TAG, "Error process table: " + table.getName() + " Exception text: " + e.getLocalizedMessage(), e);
            } finally {
                db.endTransaction();
            }

        }
    }
}
