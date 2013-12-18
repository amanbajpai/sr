package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.database.Cursor;
import com.ros.smartrocket.db.SurveyDbSchema;
import com.ros.smartrocket.db.Table;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Survey;

import java.util.ArrayList;

public class SurveysBL {

    public static void getSurveyFromDB(AsyncQueryHandler handler, Integer surveyId) {
        handler.startQuery(SurveyDbSchema.Query.TOKEN_QUERY, null, SurveyDbSchema.CONTENT_URI,
                SurveyDbSchema.Query.PROJECTION, SurveyDbSchema.Columns.ID + "=?", new String[]{String.valueOf(surveyId)},
                SurveyDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    public static void getSurveysListFromDB(AsyncQueryHandler handler, Integer radius) {
        handler.startQuery(SurveyDbSchema.QuerySurveyByDistance.TOKEN_QUERY, null, SurveyDbSchema.CONTENT_URI_SURVEY_BY_DISTANCE,
                null, " AND " + Table.TASK.getName() + "." + TaskDbSchema.Columns.DISTANCE.getName() + "<= '" + radius + "'",
                null, null);

    }

    /**
     * Conveert cursor to Task list
     *
     * @param cursor - all fields cursor
     * @return
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
     * @return
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
