package com.ros.smartrocket.db.entity;

public enum QuestionType {
    NONE(0), MULTIPLE_CHOICE(1), PHOTO(2), VALIDATION(3), REJECT(4), OPEN_COMMENT(5), SINGLE_CHOICE(6),
    VIDEO(7), NUMBER(8), INSTRUCTION(9), MASS_AUDIT(10), MAIN_SUB_QUESTION(11), AUDIO(12);

    private int typeId;

    QuestionType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
}
