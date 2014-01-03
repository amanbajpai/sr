package com.ros.smartrocket.db;

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
    public static final String CONTENT_AUTHORITY = "com.ros.smartrocket";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int ENTITY = 100;
    public static final int ENTITIES = 101;
    public static final int SURVEY_BY_DISTANCE = SurveyDbSchema.SURVEY_BY_DISTANCE;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // We're using it for quicker search
    private HashMap<String, Class> dbTables;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, "entity/" + Table.SURVEY.getName() + SURVEY_BY_DISTANCE, SURVEY_BY_DISTANCE);
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
            case SURVEY_BY_DISTANCE:
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
        String table = null;
        String[] colums = null;
        String where = null;
        String groupBy = null;
        String additionalSelection = null;

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
            case SURVEY_BY_DISTANCE:
                table = Table.SURVEY.getName() + " JOIN " + Table.TASK.getName() + " ON ("
                        + Table.TASK.getName() + "." + TaskDbSchema.Columns.SURVEY_ID.getName()
                        + " = " + Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.ID.getName() + " AND (SELECT "
                        + Table.TASK.getName() + "." + TaskDbSchema.Columns.ID.getName() + " FROM " + Table.TASK
                        .getName() + " WHERE " + TaskDbSchema.Columns.SURVEY_ID.getName()
                        + " = " + Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.ID.getName() + " ORDER BY "
                        + TaskDbSchema.Columns.DISTANCE.getName() + " ASC LIMIT 1) = " + Table.TASK.getName() + "."
                        + TaskDbSchema.Columns.ID.getName() + selection + ")";

                colums = new String[]{Table.SURVEY.getName() + "." + SurveyDbSchema.Columns._ID.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.ID.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.NAME.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.DESCRIPTION.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.LONGITUDE.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.LATITUDE.getName(),

                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.CLAIMABLE_BEFORE_LIVE.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.VIEWABLE_BEFORE_LIVE.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.CONCURRENT_CLAIMS_PER_AGENT.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.EXTERNAL_ID.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.START_DATE_TIME.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.SUSPENSION_TARGET.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.TARGET_MAXIMUM.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.TARGET_MINIMUM.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.MAXIMUM_CLAIMS_PER_AGENT.getName(),

                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.END_DATE_TIME.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.EXPECTED_END_DATE_TIME.getName(),
                        Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.EXPECTED_START_DATE_TIME.getName(),

                        Table.TASK.getName() + "." + TaskDbSchema.Columns.DISTANCE.getName(),
                        "(SELECT COUNT(*) FROM " + Table.TASK.getName() + " WHERE "
                                + TaskDbSchema.Columns.SURVEY_ID.getName() + " = " + Table.SURVEY.getName() + "."
                                + SurveyDbSchema.Columns.ID.getName() + selection + ")",
                        Table.TASK.getName() + "." + TaskDbSchema.Columns.PRICE.getName()
                };

                groupBy = Table.SURVEY.getName() + "." + SurveyDbSchema.Columns.ID.getName();

                cursor = db.query(table, colums, null, null, groupBy, null, TaskDbSchema.SORT_ORDER_DISTANCE_ASC);
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
