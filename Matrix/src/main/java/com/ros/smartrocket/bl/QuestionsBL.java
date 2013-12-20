package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.database.Cursor;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Question;

import java.util.ArrayList;

public class QuestionsBL {

    /**
     * Make request for getting Question list
     *
     * @param handler
     * @param surveyId
     */
    public static void getQuestionsListFromDB(AsyncQueryHandler handler, Integer surveyId) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.SURVEY_ID + "=?",
                new String[]{String.valueOf(surveyId)},
                TaskDbSchema.SORT_ORDER_DESC);
    }

    /**
     * Conveert cursor to Question list
     *
     * @param cursor - all fields cursor
     * @return
     */
    public static ArrayList<Question> convertCursorToQuestionList(Cursor cursor) {
        ArrayList<Question> result = new ArrayList<Question>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Question.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Conveert cursor to Question list
     *
     * @param questionId - all fields cursor
     * @return
     */
    public static Question getQuestionById(ArrayList<Question> questions, int questionId) {
        Question result = null;
        for (Question question : questions) {
            if (question.getId() == questionId) {
                result = question;
            }
        }
        return result;
    }
}
