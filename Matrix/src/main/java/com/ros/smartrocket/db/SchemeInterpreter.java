package com.ros.smartrocket.db;

import android.net.Uri;
import com.ros.smartrocket.utils.L;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class SchemeInterpreter {

    private static final String TAG = SchemeInterpreter.class.getSimpleName();
    private Uri uri;
    private String customSql;
    private Class<?> schemaClass;
    private Class<?> columnsClass;

    /**
     * WARNING! Reflection is used here!
     * Every Table must specify name and class of schema
     * Every schema must have: - CUSTOM_SQL constant for specific logic while creating tables - Columns enumeration
     * with getName() and getType() methods getName() must return String getType() must return DBType
     *
     * @param schemeClass - class scheme
     */
    public SchemeInterpreter(Class<?> schemeClass) {
        this.schemaClass = schemeClass;
        Class<?>[] classes = schemeClass.getDeclaredClasses();
        for (Class<?> innerClass : classes) {
            if (innerClass.getSimpleName().equals("Columns")) {
                columnsClass = innerClass;
                break;
            }
        }
    }

    public Uri getUri() {
        try {
            Field contentUriField = schemaClass.getDeclaredField("CONTENT_URI");
            uri = (Uri) contentUriField.get(null);
        } catch (NoSuchFieldException e) {
            L.e(TAG, e.toString(), e);
        } catch (IllegalArgumentException e) {
            L.e(TAG, e.toString(), e);
        } catch (IllegalAccessException e) {
            L.e(TAG, e.toString(), e);
        }
        return uri;
    }

    public String getCustomSql() {
        try {
            Field customSqlField = schemaClass.getDeclaredField("CUSTOM_SQL");
            customSql = (String) customSqlField.get(null);
        } catch (NoSuchFieldException e) {
            L.e(TAG, e.toString(), e);
        } catch (IllegalArgumentException e) {
            L.e(TAG, e.toString(), e);
        } catch (IllegalAccessException e) {
            L.e(TAG, e.toString(), e);
        }
        return customSql;
    }

    public HashMap<String, String> getColumnsMap() {
        LinkedHashMap<String, String> columnsMap = new LinkedHashMap<String, String>();
        try {
            Method valuesMethod;
            valuesMethod = columnsClass.getDeclaredMethod("values");
            Object[] columns = (Object[]) valuesMethod.invoke(null);
            for (Object column : columns) {
                Method getNameMethod = column.getClass().getDeclaredMethod("getName");
                String columnName = (String) getNameMethod.invoke(column);

                Method getTypeMethod = column.getClass().getDeclaredMethod("getType");
                DBType columnTypeEnum = (DBType) getTypeMethod.invoke(column);
                String columnType = columnTypeEnum.getName();

                columnsMap.put(columnName, columnType);
            }
        } catch (NoSuchMethodException e) {
            L.e(TAG, e.toString(), e);
        } catch (IllegalArgumentException e) {
            L.e(TAG, e.toString(), e);
        } catch (IllegalAccessException e) {
            L.e(TAG, e.toString(), e);
        } catch (InvocationTargetException e) {
            L.e(TAG, e.toString(), e);
        }
        return columnsMap;
    }
}
