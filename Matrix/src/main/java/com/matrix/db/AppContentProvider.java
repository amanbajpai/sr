package com.matrix.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("rawtypes")
public class AppContentProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = "com.matrix";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int ENTITY = 100;
    public static final int ENTITIES = 101;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // We're using it for quicker search
    private HashMap<String, Class> dbTables;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, "entity/*/*", ENTITY);
        matcher.addURI(authority, "entity/*", ENTITIES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = DatabaseHelper.getInstance(getContext().getApplicationContext());
        fillTableNames();
        return true;
    }

    private void fillTableNames() {
        dbTables = new HashMap<String, Class>();
        Table[] tables = Table.values();
        for (Table table : tables) {
            dbTables.put(table.getName(), table.getSchema());
        }
    }

    @Override
    public String getType(Uri uri) {
        String type = null;

        String tableName = getTable(uri);

        switch (sUriMatcher.match(uri)) {
            case ENTITY:
                type = "vnd.android.cursor.item/vnd.com.matrix.entity." + tableName;
                break;
            case ENTITIES:
                type = "vnd.android.cursor.dir/vnd.com.matrix.entity." + tableName;
                break;
            default:
                break;
        }

        return type;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        /*
         * String table = null; String[] colums = null; String where = null; String groupBy = null; String
         * additionalSelection = null;
         */

        SQLiteQueryBuilder builder = null;
        db = dbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case ENTITY:
                builder = buildExpandedSelection(uri);
                cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ENTITIES:
                //builder = buildExpandedSelection(uri);
                // cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = db.query(getTable(uri), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                break;
        }

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri resultUri = null;
        String tableName = getTable(uri);

        switch (sUriMatcher.match(uri)) {
            case ENTITIES:
                if (tableName != null) {
                    db = dbHelper.getWritableDatabase();
                    long id = db.insert(tableName, null, values);
                    resultUri = Uri.parse(BASE_CONTENT_URI + "/entity/" + tableName + "/" + id);
                }
                break;
            default:
                break;
        }

        return resultUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int count = 0;
        String tableName = getTable(uri);
        if (tableName != null) {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                for (int i = 0; i < values.length; i++) {
                    switch (sUriMatcher.match(uri)) {
                        case ENTITIES:
                            if (tableName != null) {
                                db = dbHelper.getWritableDatabase();
                                long id = db.insert(tableName, null, values[i]);
                            }
                            break;
                        default:
                            break;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        String tableName = getTable(uri);
        switch (sUriMatcher.match(uri)) {
            case ENTITY:
                if (tableName != null) {
                    db = dbHelper.getWritableDatabase();
                    count = db.update(getTable(uri), values, "_id=?", new String[]{uri.getLastPathSegment()});
                }
                break;
            case ENTITIES:
                if (tableName != null) {
                    db = dbHelper.getWritableDatabase();
                    count = db.update(getTable(uri), values, selection, selectionArgs);
                }
                break;
            default:
                break;
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        String tableName = getTable(uri);

        switch (sUriMatcher.match(uri)) {
            case ENTITY:
                if (tableName != null) {
                    db = dbHelper.getWritableDatabase();
                    count = db.delete(tableName, "id=?", new String[]{uri.getLastPathSegment()});
                }
                break;
            case ENTITIES:
                if (tableName != null) {
                    db = dbHelper.getWritableDatabase();
                    count = db.delete(tableName, selection, selectionArgs);
                }
                break;
            default:
                break;
        }

        return count;
    }

    private String getTable(final Uri uri) {
        String tableName = null;
        String entityName = null;

        switch (sUriMatcher.match(uri)) {
            case ENTITY:
                List<String> segments = uri.getPathSegments();
                entityName = segments.get(segments.size() - 2);
                if (dbTables.containsKey(entityName)) {
                    tableName = entityName;
                }
                break;
            case ENTITIES:
                entityName = uri.getLastPathSegment();
                if (dbTables.containsKey(entityName)) {
                    tableName = entityName;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown query uri: " + uri);
        }

        return tableName;
    }

    private SQLiteQueryBuilder buildExpandedSelection(Uri uri) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(getTable(uri));
        return builder;
    }
}
