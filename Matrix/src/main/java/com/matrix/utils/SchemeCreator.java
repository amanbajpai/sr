package com.matrix.utils;

import android.text.TextUtils;
import com.matrix.db.entity.BaseEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;


public class SchemeCreator {

    private static final String TAG = "SchemeCreator";

    static ArrayList<Field> setAllFields(Class cls, ArrayList<Field> fields) {
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        Class superCls = cls.getSuperclass();
        if (superCls != null) {
            setAllFields(superCls, fields);
        }
        return fields;
    }

    public static String create(Object object) {

        ArrayList<Field> fields = new ArrayList<Field>();

        setAllFields(object.getClass(), fields);
        String template = "public interface @ENTITY_NAME@DbSchema {\n    String CUSTOM_SQL = \"\";\n    Uri CONTENT_URI = AppContentProvider.BASE_CONTENT_URI.buildUpon().appendPath(\"entity\").appendPath(Table.@TABLE_NAME@.getName()).build();\n\n    public enum Columns {\n        @COLUMNS_ENUM@\n\n        private String columnName;\n        private DBType type;\n        \n        Columns(String columnName, DBType type) {\n            this.columnName = columnName;\n            this.type = type;\n        }\n\n        public String getName() {\n            return columnName;\n        }\n        \n        public DBType getType() {\n            return type;\n        }\n        \n        public String toString() {\n            return this.getName();\n        }\n    }\n    \n    public interface Query {\n        int TOKEN_QUERY = 80;\n\n        String[] PROJECTION = { \n                Table.@TABLE_NAME@.getName() + \".\"+\n                @COLUMNS_PROJECTION@\n                };\n\n        @COLUMNS_NUMBER@\n    }\n}";

        String entityName = object.getClass().getSimpleName();
        String tableName = object.getClass().getSimpleName().toUpperCase();

        template = template.replaceAll("@TABLE_NAME@", tableName);
        template = template.replaceAll("@ENTITY_NAME@", entityName);

        StringBuilder enumStringBuilder = new StringBuilder();
        StringBuilder projectionStringBuilder = new StringBuilder();
        StringBuilder columnsNumbersStringBuilder = new StringBuilder();
        addRow(enumStringBuilder, projectionStringBuilder, columnsNumbersStringBuilder, 0, "DBType.PRIMARY", "_id",
                "_ID");
        addRow(enumStringBuilder, projectionStringBuilder, columnsNumbersStringBuilder, 0, "DBType.TEXT", "id", "ID");
        int count = 2;
        for (Field field : fields) {
            String dbType = null;
            try {
                if (field.isAnnotationPresent(BaseEntity.SkipFieldInContentValues.class)) {
                    continue;
                }
                Class<?> cls = Class.forName(field.getType().getCanonicalName());
                FieldType key = FieldType.fromClass(cls);
                switch (key) {
                    case INTEGER:
                        dbType = "DBType.INT";
                        break;
                    case STRING:
                        dbType = "DBType.TEXT";
                        break;
                    case LONG:
                        dbType = "DBType.NUMERIC";
                        break;
                    case FLOAT:
                        dbType = "DBType.FLOAT";
                        break;
                    case DOUBLE:
                        dbType = "DBType.FLOAT";
                        break;
                    case BOOLEAN:
                        dbType = "DBType.INT";
                        break;
                    default:
                        continue;

                }
                field.setAccessible(true);
                String name = field.getName();
                String nameUpperCase = splitByUpperCase(name).toUpperCase();
                if (key != null) {
                    count = addRow(enumStringBuilder, projectionStringBuilder, columnsNumbersStringBuilder, count,
                            dbType, name, nameUpperCase);
                } else {
                    L.e(TAG, field.getType().getCanonicalName());
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
        projectionStringBuilder.setLength(projectionStringBuilder.length() - 2);
        enumStringBuilder.setLength(enumStringBuilder.length() - 2);
        enumStringBuilder.append(";\n");

        template = template.replaceAll("@COLUMNS_PROJECTION@", projectionStringBuilder.toString());
        template = template.replaceAll("@COLUMNS_ENUM@", enumStringBuilder.toString());
        template = template.replaceAll("@COLUMNS_NUMBER@", columnsNumbersStringBuilder.toString());

        return template;
    }

    private static int addRow(StringBuilder enumStringBuilder, StringBuilder projectionStringBuilder,
            StringBuilder columnsNumbersStringBuilder, int count, String dbType, String name, String nameUpperCase) {
        enumStringBuilder.append(nameUpperCase + "(\"" + name + "\", " + dbType + "),\n");
        projectionStringBuilder.append("Columns." + nameUpperCase + ".getName(),\n");
        columnsNumbersStringBuilder.append("int " + nameUpperCase + " = " + count++ + ";\n");
        return count;
    }

    public static String splitByUpperCase(String string) {
        String[] stringArray = string.split("(?=\\p{Lu})");
        String newStringName = TextUtils.join("_", stringArray);
        return newStringName;
    }
}
