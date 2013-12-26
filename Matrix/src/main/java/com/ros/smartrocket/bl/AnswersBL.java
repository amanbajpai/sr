package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.database.Cursor;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

public class AnswersBL {

    /**
     * Make request for getting Answer list
     *
     * @param handler
     * @param questionId
     */

    public static void getAnswersListFromDB(AsyncQueryHandler handler, Integer questionId) {
        handler.startQuery(AnswerDbSchema.Query.TOKEN_QUERY, null, AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Query.PROJECTION, AnswerDbSchema.Columns.QUESTION_ID + "=?",
                new String[]{String.valueOf(questionId)}, AnswerDbSchema.SORT_ORDER_DESC);
    }

    public static void setAnswersToDB(AsyncQueryHandler handler, Answer[] answers) {
        for (Answer answer : answers) {
            handler.startUpdate(AnswerDbSchema.Query.TOKEN_UPDATE, null, AnswerDbSchema.CONTENT_URI, answer.toContentValues(),
                    AnswerDbSchema.Columns.ID + "=?", new String[]{String.valueOf(answer.getId())});
        }
    }

    /**
     * Convert cursor to Answer array
     *
     * @param cursor - all fields cursor
     * @return
     */
    public static Answer[] convertCursorToAnswersArray(Cursor cursor) {
        Answer[] result = new Answer[cursor.getCount()];
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result[cursor.getPosition()] = Answer.fromCursor(cursor);
            }
            cursor.close();
        }
        return result;
    }


    /**
     * Get next question orderId by answer routing.
     *
     * @param question
     * @return
     */
    public static int getNextQuestionOrderId(Question question) {
        int orderId = 0;
        for (Answer answer : question.getAnswers()) {
            if (answer.isChecked()) {
                orderId = answer.getRouting();
                if (orderId == 0) {
                    orderId = question.getOrderId() + 1;
                }
                break;
            }
        }
        if (orderId == 0) {
            orderId = question.getOrderId() + 1;
        }
        return orderId;
    }
}
