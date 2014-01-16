package com.ros.smartrocket.db.entity;

import android.content.ContentValues;
import com.ros.smartrocket.utils.FieldType;
import com.ros.smartrocket.utils.L;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 7257189225671374288L;

    /**
     * Database _id field. Required for AdapterViews
     */
    private transient long _id;

    /**
     * Server UUID
     */
    private Integer Id;

    private Boolean deleted;

    /**
     * return
     */
    public static BaseEntity fromCursor() {
        return null;
    }

    private ArrayList<Field> setAllFields(Class cls, ArrayList<Field> fields) {
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        Class superCls = cls.getSuperclass();
        if (superCls != null) {
            setAllFields(superCls, fields);
        }
        return fields;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        ArrayList<Field> fields = new ArrayList<Field>();
        setAllFields(this.getClass(), fields);

        for (Field field : fields) {
            String fieldName = field.getName();
            try {
                if (field.isAnnotationPresent(SkipFieldInContentValues.class)) {
                    L.d("BaseEntity.toContentValues", "Field: \"" + fieldName + "\" from entity: \""
                            + this.getClass().getSimpleName() + "\" skipped by annotation");
                    continue;
                }
                Class<?> cls;
                if (field.getType().isAssignableFrom(Class.forName("[B"))) {
                    cls = Class.forName("[B");
                } else {
                    cls = Class.forName(field.getType().getCanonicalName());
                }
                FieldType key = FieldType.fromClass(cls);
                field.setAccessible(true);
                Object value = field.get(this);
                if (key != null && value != null) {
                    switch (key) {
                        case INTEGER:
                            contentValues.put(fieldName, (Integer) value);
                            break;
                        case STRING:
                            contentValues.put(fieldName, (String) value);
                            break;
                        case BYTE:
                            contentValues.put(fieldName, (Byte) value);
                            break;
                        case SHORT:
                            contentValues.put(fieldName, (Short) value);
                            break;
                        case LONG:
                            contentValues.put(fieldName, (Long) value);
                            break;
                        case FLOAT:
                            contentValues.put(fieldName, (Float) value);
                            break;
                        case DOUBLE:
                            contentValues.put(fieldName, (Double) value);
                            break;
                        case BOOLEAN:
                            contentValues.put(fieldName, (Boolean) value);
                            break;
                        case BLOB:
                            contentValues.put(fieldName, (byte[]) value);
                            break;
                        default:
                            break;
                    }
                } else if (key != null) {
                    contentValues.putNull(fieldName);
                } else {
                    L.d("BaseEntity.toContentValues", "Field: \"" + fieldName + "\" from entity: \""
                            + this.getClass().getSimpleName() + "\" not added to ContentValues");
                }
            } catch (ClassNotFoundException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }

        return contentValues;
    }

    public void setRandomId() {
        setId(new Random().nextInt());
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        this.Id = id;
    }

    public Boolean isDeleted() {
        return deleted == null ? (Boolean) false : deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SkipFieldInContentValues {
    }
}
