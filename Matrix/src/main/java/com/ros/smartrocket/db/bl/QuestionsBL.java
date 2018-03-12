package com.ros.smartrocket.db.bl;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.Stream;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.AskIf;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.question.QuestionType;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.TaskLocation;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class QuestionsBL {
    private static final String TAG = QuestionsBL.class.getSimpleName();
    private static final int ONE_ANSWER_OPERATOR = 1;
    private static final int NO_ANSWERS_OPERATOR = 2;
    private static final int ANY_QUESTION_OPERATOR = 3;

    private static Integer removeQuestionsFromDBbyTask(Task task) {
        return App.getInstance().getContentResolver().delete(QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Columns.WAVE_ID + "=? and "
                        + QuestionDbSchema.Columns.TASK_ID + "=? and "
                        + QuestionDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(task.getWaveId()),
                        String.valueOf(task.getId()),
                        String.valueOf(task.getMissionId())});
    }

    public static Observable<Integer> getRemoveQuestionsObservable(Task task) {
        return Observable.fromCallable(() -> removeQuestionsFromDBbyTask(task));
    }

    public static void removeQuestionsFromDB(Task task) {
        App.getInstance().getContentResolver().delete(QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Columns.WAVE_ID + "=? and "
                        + QuestionDbSchema.Columns.TASK_ID + "=? and "
                        + QuestionDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(task.getWaveId()),
                        String.valueOf(task.getId()),
                        String.valueOf(task.getMissionId())});
    }

    private static Cursor getQuestionsListFromDB(Task task, boolean includeChildQuestions) {
        String selection = QuestionDbSchema.Columns.WAVE_ID + "=? and "
                + QuestionDbSchema.Columns.TASK_ID + "=? and "
                + QuestionDbSchema.Columns.MISSION_ID + "=?";

        if (!includeChildQuestions)
            selection += " and " + QuestionDbSchema.Columns.PARENT_QUESTION_ID + " is null";
        return App.getInstance().getContentResolver().query(
                QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Query.PROJECTION,
                selection,
                new String[]{String.valueOf(task.getWaveId()), String.valueOf(task.getId()), String.valueOf(task.getMissionId())},
                QuestionDbSchema.SORT_ORDER_DESC);
    }

    public static Observable<List<Question>> getQuestionObservable(Task task, boolean includeChildQuestions) {
        return Observable.fromCallable(() -> convertCursorToQuestionList(getQuestionsListFromDB(task, includeChildQuestions)));
    }

    private static Cursor getClosingStatementQuestionFromDB(Task task) {
        return App.getInstance().getContentResolver()
                .query(QuestionDbSchema.CONTENT_URI,
                        QuestionDbSchema.Query.PROJECTION,
                        QuestionDbSchema.Columns.WAVE_ID + "=? and " +
                                QuestionDbSchema.Columns.TASK_ID + "=? and " +
                                QuestionDbSchema.Columns.TYPE + "=? and " +
                                QuestionDbSchema.Columns.MISSION_ID + "=?",
                        new String[]{String.valueOf(task.getWaveId()),
                                String.valueOf(task.getId()),
                                String.valueOf(3),
                                String.valueOf(task.getMissionId())},
                        QuestionDbSchema.SORT_ORDER_DESC
                );
    }

    public static Observable<List<Question>> closingStatementQuestionObservable(Task task) {
        return Observable.fromCallable(() -> convertCursorToQuestionList(getClosingStatementQuestionFromDB(task)));
    }

    public static Observable<List<Question>> childQuestionsListObservable(Question question) {
        return Observable.fromCallable(() -> convertCursorToQuestionList(getChildQuestionsCursorFromDB(question)));
    }

    private static Cursor getChildQuestionsCursorFromDB(Question question) {
        return App.getInstance().getContentResolver().query(
                QuestionDbSchema.CONTENT_URI,
                QuestionDbSchema.Query.PROJECTION,
                QuestionDbSchema.Columns.TASK_ID + "=? and " + QuestionDbSchema.Columns.PARENT_QUESTION_ID + "=? and "
                        + QuestionDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(question.getTaskId()),
                        String.valueOf(question.getId()),
                        String.valueOf(question.getMissionId())},
                QuestionDbSchema.SORT_ORDER_SUBQUESTIONS
        );
    }

    // ---------------------- !!! ----------------------//

    public static void setMissionId(Integer waveId, Integer taskId, Integer missionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionDbSchema.Columns.MISSION_ID.getName(), missionId);
        String where = QuestionDbSchema.Columns.WAVE_ID + "=? and " + QuestionDbSchema.Columns.TASK_ID + "=? and (" +
                QuestionDbSchema.Columns.MISSION_ID + "=? or " + QuestionDbSchema.Columns.MISSION_ID + " IS NULL )";
        String[] whereArgs = new String[]{String.valueOf(waveId), String.valueOf(taskId), String.valueOf(0)};
        App.getInstance().getContentResolver().update(QuestionDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    public static void updatePreviousQuestionOrderId(Integer questionId, Integer previousQuestionOrderId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionDbSchema.Columns.PREVIOUS_QUESTION_ORDER_ID.getName(), previousQuestionOrderId);

        String where = QuestionDbSchema.Columns.ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(questionId)};

        App.getInstance().getContentResolver().update(QuestionDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    public static void updateNextAnsweredQuestionId(Integer questionId, Integer nextAnsweredQuestionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionDbSchema.Columns.NEXT_ANSWERED_QUESTION_ID.getName(), nextAnsweredQuestionId);

        String where = QuestionDbSchema.Columns.ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(questionId)};

        App.getInstance().getContentResolver().update(QuestionDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    public static void recoverQuestionTable(Activity activity, Integer waveId, Integer taskId, Integer missionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.putNull(QuestionDbSchema.Columns.NEXT_ANSWERED_QUESTION_ID.getName());
        contentValues.putNull(QuestionDbSchema.Columns.PREVIOUS_QUESTION_ORDER_ID.getName());

        activity.getContentResolver().update(QuestionDbSchema.CONTENT_URI, contentValues,
                QuestionDbSchema.Columns.WAVE_ID + "=? and " + QuestionDbSchema.Columns.TASK_ID + "=? and "
                        + QuestionDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(waveId), String.valueOf(taskId), String.valueOf(missionId)});
    }

    public static void removeAllQuestionsFromDB(Context context) {
        context.getContentResolver().delete(QuestionDbSchema.CONTENT_URI, null, null);
    }

    public static void updateInstructionFileUri(Integer waveId, Integer taskId, Integer missionId, Integer questionId,
                                                String fileUri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionDbSchema.Columns.INSTRUCTION_FILE_URI.getName(), fileUri);

        String where = QuestionDbSchema.Columns.WAVE_ID + "=? and " + QuestionDbSchema
                .Columns.TASK_ID + "=? and " + QuestionDbSchema.Columns.ID + "=? and "
                + QuestionDbSchema.Columns.MISSION_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(waveId), String.valueOf(taskId),
                String.valueOf(questionId), String.valueOf(missionId)};

        App.getInstance().getContentResolver().update(QuestionDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    public static void updateQuestionCategories(Integer waveId, Integer taskId, Integer missionId, Integer questionId,
                                                String categories) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionDbSchema.Columns.CATEGORIES.getName(), categories);

        String where = QuestionDbSchema.Columns.WAVE_ID + "=? and " + QuestionDbSchema
                .Columns.TASK_ID + "=? and " + QuestionDbSchema.Columns.ID + "=? and "
                + QuestionDbSchema.Columns.MISSION_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(waveId), String.valueOf(taskId),
                String.valueOf(questionId), String.valueOf(missionId)};

        App.getInstance().getContentResolver().update(QuestionDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    public static List<Question> convertCursorToQuestionList(Cursor cursor) {
        List<Question> result = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Question.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    private static Question getQuestionByOrderId(List<Question> questions, int orderId) {
        Question result = null;
        for (Question question : questions) {
            if (question.getOrderId() == orderId) {
                result = question;
                break;
            }
        }

        return result;
    }

    public static Question getQuestionWithCheckConditionByOrderId(List<Question> questions, int orderId, boolean isRedo) {
        Question result = null;
        boolean continueLoop = true;
        while (result == null && continueLoop) {
            continueLoop = false;
            if (questions != null) {
                for (Question question : questions) {
                    if (question.getOrderId() == orderId) {
                        if (isRedo || checkCondition(question, questions)) {
                            result = question;
                        } else {
                            int routingOrderId = getOrderIdFromRoutingCondition(question);
                            if (routingOrderId != 0) {
                                orderId = routingOrderId;
                            } else {
                                orderId = orderId + 1;
                            }
                        }
                        continueLoop = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static boolean checkCondition(Question question, List<Question> questions) {
        AskIf[] askIfArray = question.getAskIfArray();
        boolean result = true;
        Integer previousConditionOperator = null;
        TaskLocation taskLocation = question.getTaskLocationObject();
        for (AskIf askIf : askIfArray) {
            String sourceKey = askIf.getSourceKey();
            String value = askIf.getValue();
            Integer operator = askIf.getOperator();
            Integer nextConditionOperator = askIf.getNextConditionOperator();
            boolean currentConditionResult = false;
            switch (AskIf.ConditionSourceType.getSourceTypeById(askIf.getSourceType())) {
                case LOCATION_RETAILER:
                    try {
                        currentConditionResult = operator == ONE_ANSWER_OPERATOR ?
                                (taskLocation != null && value.equals(taskLocation.getRetailerName())) :
                                (taskLocation == null || !value.equals(taskLocation.getRetailerName()));
                    } catch (Exception e) {
                        L.e(TAG, "Parse RetailerName error" + e, e);
                    }

                    break;
                case LOCATION_STATE:
                    try {
                        currentConditionResult = operator == ONE_ANSWER_OPERATOR ?
                                (taskLocation != null && value.equals(String.valueOf(taskLocation.getStateId()))) :
                                (taskLocation == null || !value.equals(String.valueOf(taskLocation.getStateId())));
                    } catch (Exception e) {
                        L.e(TAG, "Parse StateId error" + e, e);
                    }
                    break;
                case LOCATION_CITY:
                    try {
                        currentConditionResult = operator == ONE_ANSWER_OPERATOR ?
                                (taskLocation != null && value.equals(String.valueOf(taskLocation.getCityId()))) :
                                (taskLocation == null || !value.equals(String.valueOf(taskLocation.getCityId())));
                    } catch (Exception e) {
                        L.e(TAG, "Parse cityId error" + e, e);
                    }
                    break;
                case CUSTOM_FIELD:
                    try {
                        JSONObject customFieldJsonObject = new JSONObject(taskLocation.getCustomFields());
                        String customFieldValue = customFieldJsonObject.optString(sourceKey);

                        currentConditionResult = operator == ONE_ANSWER_OPERATOR ?
                                (!TextUtils.isEmpty(customFieldValue) && value.equals(customFieldValue))
                                :
                                (TextUtils.isEmpty(customFieldValue) || !value.equals(customFieldValue));
                    } catch (Exception e) {
                        L.e(TAG, "Parse customField error" + e, e);
                    }
                    break;
                case PREV_QUESTION:
                    Question previousQuestion = getQuestionByOrderId(questions, Integer.valueOf(sourceKey));
                    if (previousQuestion != null) {
                        previousQuestion.setAnswers(getAnswers(previousQuestion));
                        String answerValue = getAnswerValue(previousQuestion);
                        if (previousQuestion.getType() == QuestionType.NUMBER.getTypeId()) {
                            currentConditionResult = getCurrentConditionResultNumber(value, operator, answerValue);
                        } else if (previousQuestion.getType() == QuestionType.MULTIPLE_CHOICE.getTypeId()) {
                            currentConditionResult = getCurrentConditionResultMultiple(previousQuestion, operator, value);
                        } else {
                            currentConditionResult = getCurrentConditionResultDefault(value, operator, answerValue);
                        }
                    } else {
                        currentConditionResult = true;
                    }
                    break;
                case ROUTING:
                    continue;
                default:
                    break;
            }
            if (previousConditionOperator != null) {
                result = previousConditionOperator == 1 ? (result && currentConditionResult) : (result || currentConditionResult);
            } else {
                result = currentConditionResult;
            }
            previousConditionOperator = nextConditionOperator;
        }
        return result;
    }

    private static boolean getCurrentConditionResultMultiple(Question question, Integer operator, String answerValue) {
        boolean currentConditionResult = false;
        long conditionAnswersCount = Stream.of(question.getAnswers())
                .filter(a -> a.getChecked() && answerValue.equals(a.getValue()))
                .count();
        if (answerValue != null)
            switch (operator) {
                case ONE_ANSWER_OPERATOR:
                    long checkedAnswersCount = Stream.of(question.getAnswers())
                            .filter(Answer::getChecked).count();
                    currentConditionResult = checkedAnswersCount == 1 && conditionAnswersCount == 1;
                    break;
                case NO_ANSWERS_OPERATOR:
                    currentConditionResult = conditionAnswersCount == 0;
                    break;
                case ANY_QUESTION_OPERATOR:
                    currentConditionResult = conditionAnswersCount != 0;
                    break;
            }
        return currentConditionResult;
    }

    private static boolean getCurrentConditionResultDefault(String value, Integer operator, String answerValue) {
        boolean currentConditionResult;
        currentConditionResult = operator == ONE_ANSWER_OPERATOR ?
                (answerValue != null && value.equals(answerValue))
                :
                (answerValue != null && !value.equals(answerValue));
        return currentConditionResult;
    }

    private static boolean getCurrentConditionResultNumber(String value, Integer operator, String answerValue) {
        boolean currentConditionResult;
        String[] valuesArray = value.split("-");
        int minValue = Integer.valueOf(valuesArray[0]);
        int maxValue = Integer.valueOf(valuesArray[1]);
        if (answerValue == null) {
            currentConditionResult = false;
        } else {
            int intAnswerValue = Integer.valueOf(answerValue);
            currentConditionResult = operator == ONE_ANSWER_OPERATOR
                    ? (intAnswerValue >= minValue && intAnswerValue <= maxValue)
                    : (intAnswerValue < minValue && intAnswerValue > maxValue);
        }
        return currentConditionResult;
    }

    private static List<Answer> getAnswers(Question previousQuestion) {
        List<Answer> answers = null;
        Cursor c = App.getInstance().getContentResolver().query(AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Query.PROJECTION,
                AnswerDbSchema.Columns.QUESTION_ID + "=? and "
                        + AnswerDbSchema.Columns.TASK_ID + "=? and "
                        + AnswerDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(previousQuestion.getId()), String.valueOf(previousQuestion.getTaskId()), String.valueOf(previousQuestion.getMissionId())},
                AnswerDbSchema.SORT_ORDER_ASC);
        if (c != null) {
            answers = AnswersBL.convertCursorToAnswerList(c);
            c.close();
        }
        return answers;
    }

    private static int getOrderIdFromRoutingCondition(Question question) {
        int orderId = 0;
        AskIf[] askIfArray = question.getAskIfArray();
        for (AskIf askIf : askIfArray) {
            String value = UIUtils.getNumbersOnly(askIf.getValue());
            if (askIf.getSourceType() == AskIf.ConditionSourceType.ROUTING.getTypeId() && !TextUtils.isEmpty(value)) {
                orderId = Integer.valueOf(value);
            }
        }
        return orderId;
    }

    private static String getAnswerValue(Question question) {
        String result = null;
        if (question != null) {
            List<Answer> answers = question.getAnswers();
            if (answers != null) {
                for (Answer answer : answers) {
                    if (answer.getChecked()) {
                        result = answer.getValue();
                    }
                }
            }
        }
        return result;
    }

    public static int getQuestionsToAnswerCount(List<Question> questions) {
        int result = 0;
        for (Question question : questions) {
            if (question.getType() != 3 && question.getType() != 4) {
                result++;
            }
        }
        return result;
    }

    public static QuestionType getQuestionType(int typeId) {
        QuestionType result = QuestionType.NONE;
        for (QuestionType type : QuestionType.values()) {
            if (type.getTypeId() == typeId) {
                result = type;
                break;
            }
        }
        return result;
    }

    @Nullable
    public static Question getMainSubQuestion(List<Question> subQuestions) {
        for (Question childQuestion : subQuestions) {
            if (getQuestionType(childQuestion.getType()) == QuestionType.MAIN_SUB_QUESTION) {
                return childQuestion;
            }
        }
        return null;
    }

    @Nullable
    public static List<Question> getReDoMainSubQuestionList(List<Question> subQuestions) {
        List<Question> reDoMainSubQuestionList = new ArrayList<>();
        for (Question childQuestion : subQuestions) {
            if (getQuestionType(childQuestion.getType()) == QuestionType.MAIN_SUB_QUESTION) {
                reDoMainSubQuestionList.add(childQuestion);
            }
        }
        return reDoMainSubQuestionList;
    }

    @Nullable
    public static boolean hasReDoNotMainSub(List<Question> subQuestions, Integer productId) {
        for (Question childQuestion : subQuestions) {
            if (getQuestionType(childQuestion.getType()) != QuestionType.MAIN_SUB_QUESTION
                    && productId.equals(childQuestion.getProductId())) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public static List<Question> getInstructionQuestionList(List<Question> questions) {
        return getQuestionList(questions, QuestionType.INSTRUCTION.getTypeId());
    }

    @NonNull
    public static List<Question> getMassAuditQuestionList(List<Question> questions) {
        return getQuestionList(questions, QuestionType.MASS_AUDIT.getTypeId());
    }

    @NonNull
    private static List<Question> getQuestionList(List<Question> questions, int type) {
        List<Question> resultQuestionList = new ArrayList<>();
        for (Question question : questions) {
            if (question.getType() == type) {
                resultQuestionList.add(question);
            }
        }
        return resultQuestionList;
    }

    public static Question[] sortQuestionsByOrderId(Question[] questions) {
        Arrays.sort(questions, (o1, o2) -> o1.getOrderId() - o2.getOrderId());
        return questions;
    }

    public static int getFirstOrderId(List<Question> questions) {
        return questions != null && !questions.isEmpty() ? questions.get(0).getOrderId() : 1;
    }
}
