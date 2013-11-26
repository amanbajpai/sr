package com.matrix.db;

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
        for (DBType fildType : values()) {
            if (typeName.equals(fildType.getName())) {
                return fildType;
            }
        }
        return null;
    }
}
