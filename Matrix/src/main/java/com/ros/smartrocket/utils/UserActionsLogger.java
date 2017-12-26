package com.ros.smartrocket.utils;

import android.support.annotation.NonNull;

import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.task.Task;

import java.util.Calendar;
import java.util.TimeZone;

public final class UserActionsLogger {

    private static final String TASK_STARTED = "TASK STARTED ";
    private static final String NEW_LINE = "\n";
    private static final String DIVIDER = "/--------------------------------------/";
    private static final String TASK_FINISHED = "TASK ON VALIDATION ";
    private static final String QUESTION_OPENED = "QUESTION OPENED ";
    private static final String TASK_WITHDRAW = "TASK WITHDRAW ";
    private static final String PREVIEW = " PREVIEW ";
    private static final String BACK_TO_QUESTION = "BACK TO QUESTION :\n";
    private static final String TASK_SUBMIT_LATER = "TASK SUBMIT LATER ";

    private UserActionsLogger() {
    }

    public static void logTaskStarted(Task task, boolean isPreview) {
        if (task != null) {
            writeLogToFile(getTaskStartedText(task, isPreview));
        }
    }

    public static void logTaskOnValidation(Task task) {
        if (task != null) {
            writeLogToFile(getTaskOnValidationText(task));
        }
    }

    public static void logTaskSubmitLater(Task task) {
        if (task != null) {
            writeLogToFile(getTaskSubmitLaterText(task));
        }
    }

    public static void logTaskWithdraw(Task task) {
        if (task != null) {
            writeLogToFile(getTaskWithdrawText(task));
        }
    }

    public static void logQuestionOpened(Question question, boolean isPreview) {
        if (question != null) {
            writeLogToFile(getQuestionOpenedText(question, isPreview));
        }
    }

    public static void logPrevQuestionOpened(Question question, boolean isPreview) {
        if (question != null) {
            writeLogToFile(BACK_TO_QUESTION + getQuestionOpenedText(question, isPreview));
        }
    }

    private static String getQuestionOpenedText(Question question, boolean isPreview) {
        return QUESTION_OPENED + getPreviewText(isPreview) + getTime() + question.toString() + getDivider();
    }

    @NonNull
    private static String getTaskSubmitLaterText(Task task) {
        return TASK_SUBMIT_LATER + getTime() + task.toString() + getDivider();
    }

    @NonNull
    private static String getTaskOnValidationText(Task task) {
        return TASK_FINISHED + getTime() + task.toString() + getDivider();
    }

    @NonNull
    private static String getTaskWithdrawText(Task task) {
        return TASK_WITHDRAW + getTime() + task.toString() + getDivider();
    }

    @NonNull
    private static String getTaskStartedText(Task task, boolean isPreview) {
        return TASK_STARTED + getPreviewText(isPreview) + getTime() + task.toString() + getDivider();
    }

    private static String getPreviewText(boolean isPreview) {
        return isPreview ? PREVIEW : "";
    }

    private static String getTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().toString() + NEW_LINE;
    }

    @NonNull
    private static String getDivider() {
        return NEW_LINE + DIVIDER + NEW_LINE;
    }

    private static void writeLogToFile(String text) {
        FileProcessingManager.getInstance().saveStringToFile(text, FileProcessingManager.FILE_LOGS, FileProcessingManager.FileType.TEXT, true);
    }

}
