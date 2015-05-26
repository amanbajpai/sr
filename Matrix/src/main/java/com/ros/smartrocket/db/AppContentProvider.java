package com.ros.smartrocket.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.ros.smartrocket.BuildConfig;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("rawtypes")
public class AppContentProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    public static final int ENTITY = 100;
    public static final int ENTITIES = 101;
    public static final int WAVE_BY_DISTANCE = WaveDbSchema.WAVE_BY_DISTANCE;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // We're using it for quicker search
    private HashMap<String, Class> dbTables;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, "entity/" + Table.WAVE.getName() + WAVE_BY_DISTANCE, WAVE_BY_DISTANCE);
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
        dbTables = new HashMap<>();
        Table[] tables = Table.values();
        for (Table table : tables) {
            dbTables.put(table.getName(), table.getSchema());
        }
    }

    @Override
    public String getType(Uri uri) {
        String type = null;

        String tableName = getTable(uri);

        switch (URI_MATCHER.match(uri)) {
            case ENTITY:
                type = "vnd.android.cursor.item/vnd.com.ros.smartrocket.entity." + tableName;
                break;
            case WAVE_BY_DISTANCE:
            case ENTITIES:
                type = "vnd.android.cursor.dir/vnd.com.ros.smartrocket.entity." + tableName;
                break;
            default:
                break;
        }

        return type;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        String table;
        String[] columns;
        String where = null;
        String groupBy;
        String additionalSelection = null;

        SQLiteQueryBuilder builder;
        db = dbHelper.getReadableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case ENTITY:
                builder = buildExpandedSelection(uri);
                cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ENTITIES:
                cursor = db.query(getTable(uri), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case WAVE_BY_DISTANCE:
                table = Table.WAVE.getName() + " JOIN " + Table.TASK.getName() + " ON ("
                        + Table.TASK.getName() + "." + TaskDbSchema.Columns.WAVE_ID.getName()
                        + " = " + Table.WAVE.getName() + "." + WaveDbSchema.Columns.ID.getName() + " AND (SELECT "
                        + Table.TASK.getName() + "." + TaskDbSchema.Columns.ID.getName() + " FROM " + Table.TASK
                        .getName() + " WHERE " + TaskDbSchema.Columns.WAVE_ID.getName()
                        + " = " + Table.WAVE.getName() + "." + WaveDbSchema.Columns.ID.getName() + " AND " + Table
                        .TASK.getName() + "."
                        + TaskDbSchema.Columns.IS_MY.getName() + "=0 " /*+ " AND " + Table
                        .TASK.getName() + "."
                        + TaskDbSchema.Columns.DISTANCE.getName() + "<" + radius*/ + selection + " ORDER BY "
                        + TaskDbSchema.Columns.DISTANCE.getName() + " ASC LIMIT 1) = " + Table.TASK.getName() + "."
                        + TaskDbSchema.Columns.ID.getName() + " and " + Table.TASK.getName()
                        + "." + TaskDbSchema.Columns.IS_MY.getName() + "= 0" + selection + ")";

                columns = new String[]{Table.WAVE.getName() + "." + WaveDbSchema.Columns._ID.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.ID.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.NAME.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.DESCRIPTION.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.LONGITUDE.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.LATITUDE.getName(),

                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.CLAIMABLE_BEFORE_LIVE.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.VIEWABLE_BEFORE_LIVE.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.CONCURRENT_CLAIMS_PER_AGENT.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.EXTERNAL_WAVE_ID.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.START_DATE_TIME.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.SUSPENSION_TARGET.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.TARGET_MAXIMUM.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.TARGET_MINIMUM.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.MAXIMUM_CLAIMS_PER_AGENT.getName(),

                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.END_DATE_TIME.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.EXPECTED_END_DATE_TIME.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.EXPECTED_START_DATE_TIME.getName(),

                        Table.TASK.getName() + "." + TaskDbSchema.Columns.DISTANCE.getName(),
                        "(SELECT COUNT(*) FROM " + Table.TASK.getName() + " WHERE "
                                + TaskDbSchema.Columns.WAVE_ID.getName() + " = " + Table.WAVE.getName() + "."
                                + WaveDbSchema.Columns.ID.getName() + " and " + Table.TASK.getName()
                                + "." + TaskDbSchema.Columns.IS_MY.getName() + "= 0" + selection + ")",
                        Table.TASK.getName() + "." + TaskDbSchema.Columns.PRICE.getName(),
                        Table.TASK.getName() + "." + TaskDbSchema.Columns.ID.getName(),

                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.EXPERIENCE_OFFER.getName(),
                        Table.TASK.getName() + "." + TaskDbSchema.Columns.LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK
                                .getName(),
                        Table.WAVE.getName() + "."
                                + WaveDbSchema.Columns.EXPIRE_TIMEOUT_FOR_CLAIMED_TASK.getName(),
                        Table.WAVE.getName() + "."
                                + WaveDbSchema.Columns.PRE_CLAIMED_TASK_EXPIRE_AFTER_START.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.PHOTO_QUESTIONS_COUNT.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.NO_PHOTO_QUESTIONS_COUNT.getName(),
                        "(SELECT COUNT(*) FROM " + Table.TASK.getName() + " WHERE "
                                + TaskDbSchema.Columns.WAVE_ID.getName() + " = " + Table.WAVE.getName() + "."
                                + WaveDbSchema.Columns.ID.getName() + " AND " + TaskDbSchema.Columns.IS_HIDE.getName()
                                + "=0) == 0",
                        Table.TASK.getName() + "." + TaskDbSchema.Columns.CURRENCY_SIGN.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.ICON.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.LONG_START_DATE_TIME.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.LONG_PRE_CLAIMED_TASK_EXPIRE_AFTER_START.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.CAN_BE_PRE_CLAIMED.getName(),
                        Table.WAVE.getName() + "." + WaveDbSchema.Columns.DOWNLOAD_MEDIA_WHEN_CLAIMING_TASK.getName()

                };

                groupBy = Table.WAVE.getName() + "." + WaveDbSchema.Columns.ID.getName();

                cursor = db.query(table, columns, null, null, groupBy, null, TaskDbSchema.SORT_ORDER_END_DATE_ASC);
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

        switch (URI_MATCHER.match(uri)) {
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
                    switch (URI_MATCHER.match(uri)) {
                        case ENTITIES:
                            db = dbHelper.getWritableDatabase();
                            long id = db.insert(tableName, null, values[i]);
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
        switch (URI_MATCHER.match(uri)) {
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

        switch (URI_MATCHER.match(uri)) {
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

        switch (URI_MATCHER.match(uri)) {
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
