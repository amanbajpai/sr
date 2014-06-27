package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.text.format.DateUtils;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.Table;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.Country;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class WavesBL {

    private WavesBL() {

    }

    public static void getWaveFromDB(AsyncQueryHandler handler, Integer waveId) {
        handler.startQuery(WaveDbSchema.Query.TOKEN_QUERY, null, WaveDbSchema.CONTENT_URI,
                WaveDbSchema.Query.PROJECTION, WaveDbSchema.Columns.ID + "=?",
                new String[]{String.valueOf(waveId)}, WaveDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    public static void getNotMyTasksWavesListFromDB(AsyncQueryHandler handler, Integer radius,
                                                    boolean showHiddenTasks) {
        String withHiddenTaskWhere = showHiddenTasks ? "" : " and " + TaskDbSchema.Columns.IS_HIDE + "=0";

        handler.startQuery(WaveDbSchema.QueryWaveByDistance.TOKEN_QUERY, null,
                WaveDbSchema.CONTENT_URI_WAVE_BY_DISTANCE, null, " and " + Table.TASK.getName()
                        + "." + TaskDbSchema.Columns.IS_MY.getName() + "= 0" + withHiddenTaskWhere, null, null
        );
    }

    public static void removeAllWavesFromDB(Context context) {
        context.getContentResolver().delete(WaveDbSchema.CONTENT_URI, null, null);
    }

    public static void saveWaveAndTaskFromServer(ContentResolver contentResolver, Waves waves, Boolean isMy) {
        Location currentLocation = App.getInstance().getLocationManager().getLocation();
        Location tampLocation = new Location(LocationManager.NETWORK_PROVIDER);

        for (Wave wave : waves.getWaves()) {
            contentResolver.insert(WaveDbSchema.CONTENT_URI, wave.toContentValues());

            List<ContentValues> vals = new ArrayList<ContentValues>();
            for (Task task : wave.getTasks()) {
                task.setName(wave.getName());
                task.setDescription(wave.getDescription());
                task.setExperienceOffer(wave.getExperienceOffer());
                task.setStartedStatusSent(task.getStatusId() != null
                        && Task.TaskStatusId.none.getStatusId() != task.getStatusId()
                        && Task.TaskStatusId.claimed.getStatusId() != task.getStatusId());
                task.setLongEndDateTime(UIUtils.isoTimeToLong(task.getEndDateTime()));
                task.setLongRedoDateTime(UIUtils.isoTimeToLong(task.getRedoDate()));
                task.setLongClaimDateTime(UIUtils.isoTimeToLong(task.getClaimed()));
                task.setLongExpireTimeoutForClaimedTask(wave.getExpireTimeoutForClaimedTask() * DateUtils.HOUR_IN_MILLIS);
                task.setPreClaimedTaskExpireAfterStart(wave.getPreClaimedTaskExpireAfterStart());

                task.setPhotoQuestionsCount(wave.getPhotoQuestionsCount());
                task.setNoPhotoQuestionsCount(wave.getNoPhotoQuestionsCount());

                task.setIsMy(isMy);

                Country country = wave.getCountry();
                if (country != null) {
                    task.setCountryName(country.getName());
                }

                if (task.getLatitude() != null && task.getLongitude() != null) {
                    tampLocation.setLatitude(task.getLatitude());
                    tampLocation.setLongitude(task.getLongitude());

                    if (currentLocation != null) {
                        task.setDistance(currentLocation.distanceTo(tampLocation));
                    }
                } else {
                    task.setDistance(0f);
                }

                vals.add(task.toContentValues());
            }
            ContentValues[] bulk = new ContentValues[vals.size()];
            contentResolver.bulkInsert(TaskDbSchema.CONTENT_URI, vals.toArray(bulk));
        }
    }

    /**
     * Convert cursor to Task list
     *
     * @param cursor - all fields cursor
     * @return ArrayList<Wave>
     */
    public static ArrayList<Wave> convertCursorToWaveListByDistance(Cursor cursor) {
        ArrayList<Wave> result = new ArrayList<Wave>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Wave.fromCursorByDistance(cursor));
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Convert cursor to Wave
     *
     * @param cursor - all fields cursor
     * @return Wave
     */
    public static Wave convertCursorToWave(Cursor cursor) {
        Wave result = new Wave();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result = Wave.fromCursor(cursor);
            }
            cursor.close();
        }

        return result;
    }

    public static Wave.WaveTypes getWaveType(int typeId) {
        Wave.WaveTypes result = Wave.WaveTypes.none;
        for (Wave.WaveTypes type : Wave.WaveTypes.values()) {
            if (type.getId() == typeId) {
                result = type;
                break;
            }
        }
        return result;
    }
}