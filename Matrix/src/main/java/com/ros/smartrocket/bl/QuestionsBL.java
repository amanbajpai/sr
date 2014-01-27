package com.ros.smartrocket.bl;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.database.Cursor;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.Question;

import java.util.ArrayList;

public class QuestionsBL {

    public QuestionsBL() {

    }

    /**
     * Make request for getting Question list
     *
     * @param handler
     * @param surveyId
     */
    public static void getQuestionsListFromDB(AsyncQueryHandler handler, Integer surveyId, Integer taskId) {
        handler.startQuery(QuestionDbSchema.Query.TOKEN_QUERY, null, QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Query.PROJECTION, QuestionDbSchema.Columns.SURVEY_ID + "=? and " + QuestionDbSchema
                .Columns.TASK_ID + "=?",
                new String[]{String.valueOf(surveyId), String.valueOf(taskId)}, QuestionDbSchema.SORT_ORDER_DESC);
    }

    /**
     * Update previous question orderId
     *
     * @param questionId
     * @param previousQuestionOrderId
     */
    public static void updatePreviousQuestionOrderId(Integer questionId, Integer previousQuestionOrderId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionDbSchema.Columns.PREVIOUS_QUESTION_ORDER_ID.getName(), previousQuestionOrderId);

        String where = QuestionDbSchema.Columns.ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(questionId)};

        App.getInstance().getContentResolver().update(QuestionDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    public static void removeQuestionsFromDB(Activity activity, Integer surveyId, int taskId) {
        activity.getContentResolver().delete(QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Columns.SURVEY_ID + "=? and " + QuestionDbSchema.Columns.TASK_ID + "=?",
                new String[]{String.valueOf(surveyId), String.valueOf(taskId)});
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
     * Get Question by Id
     *
     * @param questions
     * @param questionId
     * @return
     */
    public static Question getQuestionById(ArrayList<Question> questions, int questionId) {
        Question result = null;
        for (Question question : questions) {
            if (question.getId() == questionId) {
                result = question;
                break;
            }
        }
        return result;
    }

    /**
     * Get Question by orderId
     *
     * @param questions
     * @param orderId
     * @return
     */
    public static Question getQuestionByOrderId(ArrayList<Question> questions, int orderId) {
        Question result = null;
        for (Question question : questions) {
            if (question.getOrderId() == orderId) {
                result = question;
                break;
            }
        }
        return result;
    }

    /**
     * Set Question by orderId
     *
     * @param questions
     * @param questionToSet
     * @return
     */
    public static void setQuestionByOrderId(ArrayList<Question> questions, Question questionToSet) {
        int index = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (question.getOrderId() == questionToSet.getOrderId()) {
                index = i;
                break;
            }
        }
        questions.set(index, questionToSet);
    }
}
