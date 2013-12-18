package com.ros.smartrocket.db;

import android.net.Uri;
import com.ros.smartrocket.utils.L;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class SchemeInterpretator {

    private static final String TAG = SchemeInterpretator.class.getSimpleName();
    private Uri uri;
    private LinkedHashMap<String, String> columnsMap;
    private String[] columnsArray;
    private String customSql;
    private Class<?> schemaClass;
    private Class<?> columnsClass;

    /**
     * WARNING! Reflection is used here!
     * Every Table must specify name and class of schema
     * Every schema must have: - CUSTOM_SQL constant for specific logic while creating tables - Columns enumeration with
     * getName() and getType() methods getName() must return String getType() must return DBType
     *
     * @param chemaClass
     */
    public SchemeInterpretator(Class<?> chemaClass) {
        this.schemaClass = chemaClass;
        Class<?>[] classes = chemaClass.getDeclaredClasses();
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
            L.e(TAG, e.toString());
        } catch (IllegalArgumentException e) {
            L.e(TAG, e.toString());
        } catch (IllegalAccessException e) {
            L.e(TAG, e.toString());
        }
        return uri;
    }

    public String getCustomSql() {
        try {
            Field customSqlField = schemaClass.getDeclaredField("CUSTOM_SQL");
            customSql = (String) customSqlField.get(null);
        } catch (NoSuchFieldException e) {
            L.e(TAG, e.toString());
        } catch (IllegalArgumentException e) {
            L.e(TAG, e.toString());
        } catch (IllegalAccessException e) {
            L.e(TAG, e.toString());
        }
        return customSql;
    }

    public HashMap<String, String> getColumnsMap() {
        columnsMap = new LinkedHashMap<String, String>();
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
            L.e(TAG, e.toString());
        } catch (IllegalArgumentException e) {
            L.e(TAG, e.toString());
        } catch (IllegalAccessException e) {
            L.e(TAG, e.toString());
        } catch (InvocationTargetException e) {
            L.e(TAG, e.toString());
        }
        return columnsMap;
    }


    public String[] getColumnsNameArray() {
        try {
            Method valuesMethod;
            valuesMethod = columnsClass.getDeclaredMethod("values");
            Object[] columns = (Object[]) valuesMethod.invoke(null);

            columnsArray = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                Object column = columns[i];
                Method getNameMethod = column.getClass().getDeclaredMethod("getName");
                String columnName = (String) getNameMethod.invoke(column);

                columnsArray[i] = columnName;
            }
        } catch (NoSuchMethodException e) {
            L.e(TAG, e.toString());
        } catch (IllegalArgumentException e) {
            L.e(TAG, e.toString());
        } catch (IllegalAccessException e) {
            L.e(TAG, e.toString());
        } catch (InvocationTargetException e) {
            L.e(TAG, e.toString());
        }
        return columnsArray;
    }
}
