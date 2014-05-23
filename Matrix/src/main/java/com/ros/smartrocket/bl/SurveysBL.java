package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.SurveyDbSchema;
import com.ros.smartrocket.db.Table;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Survey;
import com.ros.smartrocket.db.entity.Surveys;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

public class SurveysBL {

    public SurveysBL() {

    }

    public static void getSurveyFromDB(AsyncQueryHandler handler, Integer surveyId) {
        handler.startQuery(SurveyDbSchema.Query.TOKEN_QUERY, null, SurveyDbSchema.CONTENT_URI,
                SurveyDbSchema.Query.PROJECTION, SurveyDbSchema.Columns.ID + "=?",
                new String[]{String.valueOf(surveyId)}, SurveyDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    public static void getNotMyTasksSurveysListFromDB(AsyncQueryHandler handler, Integer radius, boolean showHiddenTasks) {
        String withHiddenTaskWhere = showHiddenTasks ? "" : " and " + TaskDbSchema.Columns.IS_HIDE + "=0";

        handler.startQuery(SurveyDbSchema.QuerySurveyByDistance.TOKEN_QUERY, null,
                SurveyDbSchema.CONTENT_URI_SURVEY_BY_DISTANCE, null, " and " + Table.TASK.getName() + "."
                        + TaskDbSchema.Columns.DISTANCE.getName() + "<= '" + radius + "' and  " + Table.TASK.getName()
                        + "." + TaskDbSchema.Columns.IS_MY.getName() + "= 0" + withHiddenTaskWhere, null, null
        );
    }

    public static void removeAllSurveysFromDB(Context context) {
        context.getContentResolver().delete(SurveyDbSchema.CONTENT_URI, null, null);
    }

    public static void saveSurveyAndTaskFromServer(ContentResolver contentResolver, Surveys surveys, Boolean isMy) {
        Location currentLocation = App.getInstance().getLocationManager().getLocation();
        Location tampLocation = new Location(LocationManager.NETWORK_PROVIDER);

        for (Survey survey : surveys.getSurveys()) {
            contentResolver.insert(SurveyDbSchema.CONTENT_URI, survey.toContentValues());

            ArrayList<ContentValues> vals = new ArrayList<ContentValues>();
            for (Task task : survey.getTasks()) {
                task.setName(survey.getName());
                task.setDescription(survey.getDescription());
                task.setExperienceOffer(survey.getExperienceOffer());
                task.setLongEndDateTime(UIUtils.isoTimeToLong(task.getEndDateTime()));

                if (task.getLatitude() != null && task.getLongitude() != null) {
                    tampLocation.setLatitude(task.getLatitude());
                    tampLocation.setLongitude(task.getLongitude());
                    task.setIsMy(isMy);

                    if (currentLocation != null) {
                        task.setDistance(currentLocation.distanceTo(tampLocation));
                    }
                } else {
                    task.setDistance(0f);
                    task.setIsMy(isMy);
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
     * @return ArrayList<Survey>
     */
    public static ArrayList<Survey> convertCursorToSurveyListByDistance(Cursor cursor) {
        ArrayList<Survey> result = new ArrayList<Survey>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Survey.fromCursorByDistance(cursor));
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Convert cursor to Survey
     *
     * @param cursor - all fields cursor
     * @return Survey
     */
    public static Survey convertCursorToSurvey(Cursor cursor) {
        Survey result = new Survey();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result = Survey.fromCursor(cursor);
            }
            cursor.close();
        }

        return result;
    }
}
