package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by macbook on 21.10.15.
 */
public class PushSettings extends BaseEntity {

    @SerializedName("AgentIds")
    private ArrayList<Integer> agentIds = new ArrayList<>();

    public ArrayList<Integer> getAgentIds() {
        return agentIds;
    }

    public void setAgentIds(ArrayList<Integer> agentIds) {
        this.agentIds = agentIds;
    }

    public void addId(int id){
        agentIds.add((Integer)id);
    }
}
