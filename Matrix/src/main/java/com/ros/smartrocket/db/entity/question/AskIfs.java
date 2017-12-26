package com.ros.smartrocket.db.entity.question;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class AskIfs extends BaseEntity {
    private static final long serialVersionUID = 5410835468654163958L;

    @SerializedName("AskIf")
    private AskIf[] askIfs;

    public AskIfs() {
    }

    public AskIf[] getAskIfs() {
        return askIfs;
    }
}
