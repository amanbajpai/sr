package com.ros.smartrocket.db;

public enum DBType {
    PRIMARY("INTEGER PRIMARY KEY AUTOINCREMENT"),
    INT("INTEGER DEFAULT 0"),
    INT_DEF("INTEGER DEFAULT -1"),
    FLOAT("FLOAT"),
    TEXT("TEXT"),
    NUMERIC("NUMERIC"),
    BLOB("BLOB");

    private String name;

    DBType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DBType fromName(String typeName) {
        for (DBType fieldType : values()) {
            if (typeName.equals(fieldType.getName())) {
                return fieldType;
            }
        }
        return null;
    }
}
