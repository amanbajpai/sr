package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.db.Table;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.Country;
import com.ros.smartrocket.db.entity.Project;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.utils.ChinaTransformLocation;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;

public class WavesBL {

    private WavesBL() {

    }

    private static Cursor getNotMyWavesListCursor(boolean showHiddenTasks) {
        String withHiddenTaskWhere = showHiddenTasks ? "" : " and " + TaskDbSchema.Columns.IS_HIDE + "=0";
        ContentResolver resolver = App.getInstance().getContentResolver();
        return resolver.query(WaveDbSchema.CONTENT_URI_WAVE_BY_DISTANCE, null, withHiddenTaskWhere, null, null);
    }

    public static Observable<List<Wave>> getNotMyWavesListObservableFromDB(boolean showHiddenTasks) {
        return Observable.fromCallable(() -> convertCursorToWaveListByDistance(getNotMyWavesListCursor(showHiddenTasks)));
    }

    private static Cursor getWaveFromDBbyIdCursor(Integer waveId) {
        return App.getInstance().getContentResolver()
                .query(WaveDbSchema.CONTENT_URI, WaveDbSchema.Query.PROJECTION,
                        WaveDbSchema.Columns.ID + "=?", new String[]{String.valueOf(waveId)},
                        WaveDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    public static Observable<Wave> getWaveFromDBbyIdObservable(Integer waveId) {
        return Observable.fromCallable(() -> convertCursorToWave(getWaveFromDBbyIdCursor(waveId)));
    }

    //-------------------------!!!!--------------------------//

    public static void getWaveWithNearTaskFromDB(AsyncQueryHandler handler, Integer waveId) {
        String where = " and " + Table.WAVE.getName() + "."
                + WaveDbSchema.Columns.ID.getName() + "=" + waveId;
        handler.startQuery(WaveDbSchema.QueryWaveByDistance.TOKEN_QUERY, null,
                WaveDbSchema.CONTENT_URI_WAVE_BY_DISTANCE, null, where, null, null
        );
    }

    public static void removeAllWavesFromDB(Context context) {
        context.getContentResolver().delete(WaveDbSchema.CONTENT_URI, null, null);
    }

    public static void saveWaveAndTaskFromServer(ContentResolver contentResolver, Waves waves, Boolean isMy) {
        Location currentLocation = App.getInstance().getLocationManager().getLocation();
        Location tampLocation = new Location(LocationManager.NETWORK_PROVIDER);

        for (Wave wave : waves.getWaves()) {

            Project project = wave.getProject();
            String projectIcon = project != null ? project.getIcon() : null;

            if (TextUtils.isEmpty(wave.getIcon())) {
                wave.setIcon(projectIcon);
            }

            Country country = wave.getCountry();
            String countryName = country != null ? country.getName() : null;

            long longPreClaimedTaskExpireAfterStart = wave.getPreClaimedTaskExpireAfterStart() * DateUtils.HOUR_IN_MILLIS;
            long longExpireTimeoutForClaimedTask = wave.getExpireTimeoutForClaimedTask() * DateUtils.HOUR_IN_MILLIS;

            wave.setLongPreClaimedTaskExpireAfterStart(longPreClaimedTaskExpireAfterStart);
            wave.setLongExpireTimeoutForClaimedTask(longExpireTimeoutForClaimedTask);

            wave.setLongStartDateTime(UIUtils.isoTimeToLong(wave.getStartDateTime()));
            contentResolver.insert(WaveDbSchema.CONTENT_URI, wave.toContentValues());

            List<ContentValues> vals = new ArrayList<>();
            for (Task task : wave.getTasks()) {
                task.setName(wave.getName());
                task.setIcon(projectIcon);
                task.setCountryName(countryName);
                task.setDescription(wave.getDescription());
                if (task.getExperienceOffer() == null || task.getExperienceOffer() == 0.0) {
                    task.setExperienceOffer(wave.getExperienceOffer());
                }
                task.setStartedStatusSent(task.getStatusId() != null
                        && Task.TaskStatusId.NONE.getStatusId() != task.getStatusId()
                        && Task.TaskStatusId.CLAIMED.getStatusId() != task.getStatusId());

                task.setLongStartDateTime(UIUtils.isoTimeToLong(task.getStartDateTime()));
                task.setLongEndDateTime(UIUtils.isoTimeToLong(task.getEndDateTime()));
                task.setLongExpireDateTime(UIUtils.isoTimeToLong(task.getExpireDateTime()));
                task.setLongClaimDateTime(UIUtils.isoTimeToLong(task.getClaimed()));
                task.setLongRedoDateTime(UIUtils.isoTimeToLong(task.getRedoDate()));

                task.setLongExpireTimeoutForClaimedTask(longExpireTimeoutForClaimedTask);
                task.setLongPreClaimedTaskExpireAfterStart(longPreClaimedTaskExpireAfterStart);

                task.setApproxMissionDuration(wave.getApproxMissionDuration());

                task.setIsMy(isMy);

                if (task.getLatitude() != null && task.getLongitude() != null) {
                    tampLocation.setLatitude(task.getLatitude());
                    tampLocation.setLongitude(task.getLongitude());

                    if (Config.USE_BAIDU) {
                        ChinaTransformLocation.transformFromWorldToBaiduLocation(tampLocation);

                        task.setLatitude(tampLocation.getLatitude());
                        task.setLongitude(tampLocation.getLongitude());
                    }

                    if (currentLocation != null) {
                        task.setDistance(currentLocation.distanceTo(tampLocation));
                    }
                } else {
                    task.setDistance(0f);
                }

                vals.add(task.toContentValues());
            }
            ContentValues[] bulk = new ContentValues[vals.size()];
            int inserted = contentResolver.bulkInsert(TaskDbSchema.CONTENT_URI, vals.toArray(bulk));
            L.i("WAVES BL", "INSERTED " + inserted + " BULK SIZE " + bulk.length);
        }
    }


    private static List<Wave> convertCursorToWaveListByDistance(Cursor cursor) {
        List<Wave> result = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Wave wave = Wave.fromCursorByDistance(cursor);
                result.add(wave);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Convert cursor to Task list
     *
     * @param cursor - all fields cursor
     * @return Wave
     */
    public static Wave convertCursorToWaveWithTask(Cursor cursor) {
        Wave result = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = Wave.fromCursorByDistance(cursor);
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

    public static boolean isPreClaimWave(Wave wave) {
        return UIUtils.isoTimeToLong(wave.getStartDateTime()) > Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Update wave
     */
    public static void updateWave(int waveId, int missionSize) {
        String where = WaveDbSchema.Columns.ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(waveId)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(WaveDbSchema.Columns.MISSION_SIZE.getName(), missionSize);
        App.getInstance().getContentResolver().update(WaveDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }
}
