package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionsBL {

    private QuestionsBL() {

    }

    /**
     * Make request for getting Question list
     *
     * @param handler - Handler for getting response from DB
     * @param waveId  - current waveId
     */
    public static void getQuestionsListFromDB(AsyncQueryHandler handler, Integer waveId, Integer taskId) {
        handler.startQuery(QuestionDbSchema.Query.TOKEN_QUERY, null, QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Query.PROJECTION, QuestionDbSchema.Columns.WAVE_ID + "=? and " + QuestionDbSchema
                        .Columns.TASK_ID + "=?",
                new String[]{String.valueOf(waveId), String.valueOf(taskId)}, QuestionDbSchema.SORT_ORDER_DESC
        );
    }

    /**
     * Make request for getting Closing Statement Question
     *
     * @param handler - Handler for getting response from DB
     * @param waveId  - current waveId
     * @param taskId  - current taskId
     */
    public static void getClosingStatementQuestionFromDB(AsyncQueryHandler handler, Integer waveId, Integer taskId) {
        handler.startQuery(QuestionDbSchema.Query.TOKEN_QUERY, null, QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Query.PROJECTION, QuestionDbSchema.Columns.WAVE_ID + "=? and " + QuestionDbSchema
                        .Columns.TASK_ID + "=? and " + QuestionDbSchema.Columns.TYPE + "=?",
                new String[]{String.valueOf(waveId), String.valueOf(taskId), String.valueOf(3)},
                QuestionDbSchema.SORT_ORDER_DESC
        );
    }

    /**
     * Update previous question orderId
     *
     * @param questionId              - current questionId
     * @param previousQuestionOrderId - orderId of previous question
     */
    public static void updatePreviousQuestionOrderId(Integer questionId, Integer previousQuestionOrderId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionDbSchema.Columns.PREVIOUS_QUESTION_ORDER_ID.getName(), previousQuestionOrderId);

        String where = QuestionDbSchema.Columns.ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(questionId)};

        App.getInstance().getContentResolver().update(QuestionDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    public static void removeQuestionsFromDB(Context context, Integer waveId, int taskId) {
        context.getContentResolver().delete(QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Columns.WAVE_ID + "=? and " + QuestionDbSchema.Columns.TASK_ID + "=?",
                new String[]{String.valueOf(waveId), String.valueOf(taskId)});
    }

    public static void removeQuestionsByWaveId(Context context, Integer waveId) {
        context.getContentResolver().delete(QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Columns.WAVE_ID + "=?", new String[]{String.valueOf(waveId)});
    }

    public static void removeAllQuestionsFromDB(Context context) {
        context.getContentResolver().delete(QuestionDbSchema.CONTENT_URI, null, null);
    }

    /**
     * Convert cursor to Question list
     *
     * @param cursor - all fields cursor
     * @return ArrayList<Question>
     */
    public static List<Question> convertCursorToQuestionList(Cursor cursor) {
        List<Question> result = new ArrayList<Question>();
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
    /*public static Question getQuestionById(ArrayList<Question> questions, int questionId) {
        Question result = null;
        for (Question question : questions) {
            if (question.getId() == questionId) {
                result = question;
                break;
            }
        }
        return result;
    }*/

    /**
     * Get Question by orderId
     *
     * @param questions - question list
     * @param orderId   - orderId to select
     * @return Question
     */
    public static Question getQuestionByOrderId(List<Question> questions, int orderId) {
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
     * Get Questions to answer count
     *
     * @param questions - question list
     * @return Integer
     */
    public static int getQuestionsToAnswerCount(List<Question> questions) {
        int result = 0;
        for (Question question : questions) {
            if (question.getType() != 3 && question.getType() != 4) {
                result++;
            }
        }
        return result;
    }

    /**
     * Set Question by orderId
     *
     * @param questions
     * @param questionToSet
     */
    /*public static void setQuestionByOrderId(ArrayList<Question> questions, Question questionToSet) {
        int index = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (question.getOrderId() == questionToSet.getOrderId()) {
                index = i;
                break;
            }
        }
        questions.set(index, questionToSet);
    }*/

    /**
     * Set Question by orderId
     *
     * @param typeId - question type id
     * @return QuestionType
     */
    public static Question.QuestionType getQuestionType(int typeId) {
        Question.QuestionType result = Question.QuestionType.none;
        for (Question.QuestionType type : Question.QuestionType.values()) {
            if (type.getTypeId() == typeId) {
                result = type;
                break;
            }
        }
        return result;
    }
}
