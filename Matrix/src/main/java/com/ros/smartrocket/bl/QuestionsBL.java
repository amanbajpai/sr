package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.AskIf;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.TaskLocation;
import com.ros.smartrocket.utils.L;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionsBL {
    private static final String TAG = QuestionsBL.class.getSimpleName();

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
     * Get Question by orderId
     *
     * @param questions - question list
     * @param orderId   - orderId to select
     * @return Question
     */
    public static Question getQuestionWithCheckConditionByOrderId(List<Question> questions, int orderId) {
        int i = 0; //Not more 50 redirects by routing
        Question result = null;
        while (result == null && i < 50) {
            for (Question question : questions) {
                if (question.getOrderId() == orderId) {
                    int previousQuestionOrderId = question.getPreviousQuestionOrderId() != 0 ? question.getPreviousQuestionOrderId() : 1;
                    Question previousQuestion = getQuestionByOrderId(questions, previousQuestionOrderId);
                    if (checkCondition(question, previousQuestion)) {
                        result = question;
                    } else {
                        orderId = getOrderIdFromRoutingCondition(question);
                        i++;
                    }
                    break;
                }
            }
        }
        return result;
    }

    public static boolean checkCondition(Question question, Question previousQuestion) {
        AskIf[] askIfArray = question.getAskIfArray();
        boolean result = askIfArray.length == 0;
        TaskLocation taskLocation = question.getTaskLocationObject();
        String answerValue = getAnswerValue(previousQuestion);

        askifloop:
        for (AskIf askIf : askIfArray) {
            String sourceKey = askIf.getSourceKey();
            String value = askIf.getValue();
            Integer operator = askIf.getOperator();
            Integer nextConditionOperator = askIf.getNextConditionOperator();

            boolean currentConditionResult = false;

            switch (AskIf.ConditionSourceType.getSourceTypeById(askIf.getSourceType())) {
                case LOCATION_RETAILER:
                    currentConditionResult = operator == 1 ? value.equals(taskLocation.getRetailerName()) : !value.equals(taskLocation.getRetailerName());

                    break;
                case LOCATION_STATE:
                    currentConditionResult = operator == 1 ? value.equals(taskLocation.getState()) : !value.equals(taskLocation.getState());

                    break;
                case LOCATION_CITY:
                    currentConditionResult = operator == 1 ? value.equals(taskLocation.getCity()) : !value.equals(taskLocation.getCity());

                    break;
                case CUSTOM_FIELD:
                    try {
                        JSONObject customFieldJsonObject = new JSONObject(taskLocation.getCustomFields());
                        String customFieldValue = (String) customFieldJsonObject.get(sourceKey);

                        currentConditionResult = operator == 1 ? value.equals(customFieldValue) : !value.equals(customFieldValue);
                    } catch (Exception e) {
                        L.e(TAG, "Parse customField error" + e, e);
                    }
                    break;
                case PREV_QUESTION:
                    currentConditionResult = operator == 1 ? value.equals(answerValue) : !value.equals(answerValue);
                    break;
                case ROUTING:
                    break askifloop;
                default:
                    L.e(TAG, "WRONG condition type: " + askIf.getSourceType());
                    break;
            }

            result = nextConditionOperator == 1 ? (result && currentConditionResult) : (result || currentConditionResult);

        }

        return result;
    }

    public static int getOrderIdFromRoutingCondition(Question question) {
        int orderId = 0;
        AskIf[] askIfArray = question.getAskIfArray();
        for (AskIf askIf : askIfArray) {
            if (askIf.getSourceType() == AskIf.ConditionSourceType.ROUTING.getTypeId()) {
                orderId = Integer.valueOf(askIf.getValue());
            }
        }
        return orderId;
    }

    public static String getAnswerValue(Question question) {
        String result = null;
        Answer[] answers = question.getAnswers();

        if (answers != null) {
            for (Answer answer : answers) {
                if (answer.getChecked()) {
                    result = answer.getValue();
                }
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
        Question.QuestionType result = Question.QuestionType.NONE;
        for (Question.QuestionType type : Question.QuestionType.values()) {
            if (type.getTypeId() == typeId) {
                result = type;
                break;
            }
        }
        return result;
    }
}
