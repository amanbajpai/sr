package com.ros.smartrocket.utils;

import android.support.annotation.NonNull;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;

import java.util.Calendar;
import java.util.TimeZone;

public final class UserActionsLogger {

    private static final String TASK_STARTED = "TASK STARTED ";
    private static final String NEW_LINE = "\n";
    private static final String DIVIDER = "/--------------------------------------/";
    private static final String TASK_FINISHED = "TASK ON VALIDATION ";
    private static final String QUESTION_OPENED = "QUESTION OPENED ";
    private static final String TASK_WITHDRAW = "TASK WITHDRAW ";

    private UserActionsLogger() {
    }

    public static void logTaskStarted(Task task) {
        writeLogToFile(getTaskStartedText(task));
    }

    public static void logTaskOnValidation(Task task) {
        writeLogToFile(getTaskOnValidationText(task));
    }

    public static void logTaskWithdraw(Task task) {
        writeLogToFile(getTaskWithdrawText(task));
    }

    public static void logQuestionOpened(Question question) {
        writeLogToFile(getQuestionOpenedText(question));
    }

    private static String getQuestionOpenedText(Question question) {
        return QUESTION_OPENED + getTime() + question.toString() + getDivider();
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
    private static String getTaskStartedText(Task task) {
        return TASK_STARTED + getTime() + task.toString() + getDivider();
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
