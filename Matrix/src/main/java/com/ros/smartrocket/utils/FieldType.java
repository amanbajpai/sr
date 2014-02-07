package com.ros.smartrocket.utils;


public enum FieldType {
    INTEGER(Integer.class),
    STRING(String.class),
    BOOLEAN(Boolean.class),
    LONG(Long.class),
    BYTE(Byte.class),
    SHORT(Short.class),
    FLOAT(Float.class),
    DOUBLE(Double.class),
    BLOB(byte[].class);

    private Class<?> cls;

    private FieldType(Class<?> cls) {
        this.cls = cls;
    }

    public Class<?> getCls() {
        return cls;
    }

    public static FieldType fromClass(Class<?> cls) {
        if (cls != null) {
            for (FieldType b : FieldType.values()) {
                if (cls.equals(b.getCls())) {
                    return b;
                }
            }
        }
        return null;
    }
}
