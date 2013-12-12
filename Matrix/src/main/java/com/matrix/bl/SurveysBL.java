package com.matrix.bl;

import android.content.AsyncQueryHandler;
import android.database.Cursor;
import com.matrix.db.SurveyDbSchema;
import com.matrix.db.Table;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Survey;
import com.matrix.db.entity.Task;

import java.util.ArrayList;

public class SurveysBL {

    public static void getSurveyFromDB(AsyncQueryHandler handler, Integer surveyId) {
        handler.startQuery(SurveyDbSchema.Query.TOKEN_QUERY, null, SurveyDbSchema.CONTENT_URI,
                SurveyDbSchema.Query.PROJECTION, SurveyDbSchema.Columns.ID + "=?", new String[]{String.valueOf(surveyId)},
                SurveyDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    public static void getSurveysListFromDB(AsyncQueryHandler handler, Integer radius) {
        handler.startQuery(SurveyDbSchema.QuerySurveyByDistance.TOKEN_QUERY, null, SurveyDbSchema.CONTENT_URI_SURVEY_BY_DISTANCE,
                null, Table.TASK.getName() + "." + TaskDbSchema.Columns.DISTANCE.getName() + "<= ?",
                new String[]{String.valueOf(radius)}, SurveyDbSchema.SORT_ORDER_DESC);

    }

    /**
     * Conveert cursor to Task list
     *
     * @param cursor - all fields cursor
     * @return
     */
    public static ArrayList<Survey> convertCursorToSurveyList(Cursor cursor) {
        ArrayList<Survey> result = new ArrayList<Survey>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Survey.fromCursor(cursor));
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
